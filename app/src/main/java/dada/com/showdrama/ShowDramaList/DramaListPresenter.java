package dada.com.showdrama.ShowDramaList;

import android.util.Log;

import androidx.paging.PagedList;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Executors;

import dada.com.showdrama.Base.BasePresenter;
import dada.com.showdrama.Data.Retrofit.ApiCallback;
import dada.com.showdrama.Model.Drama;
import dada.com.showdrama.Model.DramaPack;
import dada.com.showdrama.Util.Constant;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;

public class DramaListPresenter extends BasePresenter<IDramaListView>{
    private DramaListRepository dramaListRepository;
    public DramaListPresenter(IDramaListView iView) {
        attachView(iView);
    }
    private static final String TAG = "dramalist_presenter";



    public boolean initFromEndState(){
        String lastquery = mvpView.getLastTimeQuery();
        if (!lastquery.equals("")) return true;
        return false;
    }

    public void searchDramaFromKeyWord(String searchkey){
        if(checkSearchKeywordNotEnpty(searchkey)){
            String searchKeyInDb = getSearchKeyword(searchkey);
            if (dramaListRepository == null) dramaListRepository = new DramaListRepository();
            mvpView.showLoading();
            DisposableObserver dataObserver = new DisposableObserver<PagedList<Drama>>() {
                @Override
                public void onNext(PagedList<Drama> dramas) {
                    mvpView.getDramasSuccess(dramas);
                    mvpView.hideLoading();
                }

                @Override
                public void onError(Throwable e) {
                    Log.i(TAG, "onError: data observer "+e.getMessage());
                    mvpView.hideLoading();
                    mvpView.getDramaFail(e.getMessage());
                }

                @Override
                public void onComplete() {
                    mvpView.hideLoading();
                }
            };

            addSubScribe(dramaListRepository.getDramaFromKeyWord(searchKeyInDb),dataObserver);
        }else{
            getAllDramaFromDb();
        }
    }


    public String getSearchKeyword(String searchText) {
        searchText = "%"+searchText+"%";

        return searchText;
    }


    public boolean checkSearchKeywordNotEnpty(String searchKeyword) {
        if (searchKeyword.equals("")) return false;
        return true;
    }

    public void updateDataFromNet(){
        addSubScribe(dramaListRepository.getDataRemote().doOnNext(new Consumer<DramaPack>() {
            @Override
            public void accept(DramaPack dramaPack) throws Exception {
                Log.i(TAG, "accept: 先存進 db");
                dramaListRepository.insertDataInDb(dramaPack.getData());
            }
        }), new ApiCallback<DramaPack>() {
            @Override
            public void onSuccess(DramaPack dramaPack) {
                Log.i(TAG, "accept: 後決定是否更新畫面");
                if (!mvpView.ifDramaListHaveData()){
                    if (dramaPack.getData()!= null) return;
                    if (dramaPack.getData().size()> 0 ) {
                        PagedList<Drama> pagedDramas = getDramas(dramaPack);
                        mvpView.getDramasSuccess(pagedDramas);
                        Log.i(TAG, "update data from net");
                    }
                }
                // 想知道目前 View 裡面是有沒有資料的
            }

            @Override
            public void onFail(String msg) {

            }

            @Override
            public void onFinish() {
                mvpView.finishRefresh();
            }
        });
    }

    @NotNull
    private PagedList<Drama> getDramas(DramaPack dramaPack) {
        int initPageSize = (dramaPack.getData().size()> Constant.PAGESIZE)
                ? Constant.PAGESIZE :dramaPack.getData().size();

        PagedList.Config myConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(initPageSize)
                .setPageSize(Constant.PAGESIZE)
                .build();
        List<Drama> dramas = dramaPack.getData();
        DramaListProvider provider = new DramaListProvider(dramas);
        DramaDataSource dataSource = new DramaDataSource(provider);

        return new PagedList.Builder<Integer, Drama>(dataSource, myConfig)
                .setInitialKey(0)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .setNotifyExecutor(new MainThreadExecutor())
                .build();
    }

    public void loadDramaData(){
        String lastQuery = mvpView.getLastTimeQuery();
        mvpView.setLastQuery(lastQuery);
        updateDataFromNet();

    }

    private void getAllDramaFromDb() {
        if (dramaListRepository == null) dramaListRepository = new DramaListRepository();
        mvpView.showLoading();
        DisposableObserver dataObserver = new DisposableObserver<PagedList<Drama>>() {
            @Override
            public void onNext(PagedList<Drama> dramas) {
                mvpView.getDramasSuccess(dramas);
                mvpView.hideLoading();
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: data observer "+e.getMessage());
                mvpView.hideLoading();
                mvpView.getDramaFail(e.getMessage());
            }

            @Override
            public void onComplete() {
                mvpView.hideLoading();
            }
        };

        addSubScribe(dramaListRepository.getDatalocal(),dataObserver);
    }

}
