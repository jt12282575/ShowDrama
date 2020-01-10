package dada.com.showdrama.ShowDramaList;

import android.util.Log;

import androidx.paging.PagedList;

import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;

import java.util.List;

import dada.com.showdrama.Base.BasePresenter;
import dada.com.showdrama.Data.ApiCallback;
import dada.com.showdrama.Model.Drama;
import dada.com.showdrama.Model.DramaPack;
import dada.com.showdrama.Util.Constant;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class DramaListPresenter extends BasePresenter<IDramaListView>{
    private DramaListRepository dramaListRepository;
    public DramaListPresenter(IDramaListView iView) {
        attachView(iView);
    }
    private static final String TAG = "dramalist_presenter";
    public void loadData(){
        if (dramaListRepository == null) dramaListRepository = new DramaListRepository();
        mvpView.showLoading();
        DisposableObserver dataObserver = new DisposableObserver<PagedList<Drama>>() {
            @Override
            public void onNext(PagedList<Drama> dramas) {
                mvpView.getDramasSuccess(dramas);
            }

            @Override
            public void onError(Throwable e) {
                mvpView.getDramaFail(e.getMessage());
            }

            @Override
            public void onComplete() {
                mvpView.hideLoading();
            }
        };

        addSubScribe(dataObserver);

        mvpView.getNetworkState().flatMap(new Function<Connectivity, ObservableSource<PagedList<Drama>>>() {
            @Override
            public ObservableSource<PagedList<Drama>> apply(Connectivity connectivity) throws Exception {
                if (connectivity.available()){
                    Log.i(TAG, "從網路讀資料: ");
                    return dramaListRepository.getDataRemote().map(new Function<DramaPack, PagedList<Drama>>() {
                        @Override
                        public PagedList<Drama> apply(DramaPack dramaPack) throws Exception {
                            PagedList.Config myConfig = new PagedList.Config.Builder()
                                    .setInitialLoadSizeHint(Constant.PAGESIZE)
                                    .setPageSize(Constant.PAGESIZE)
                                    .build();
                            List<Drama> dramas = dramaPack.getData();
                            DramaListProvider provider = new DramaListProvider(dramas);
                            DramaDataSource dataSource = new DramaDataSource(provider);
                            PagedList<Drama> pagedDramas = new PagedList.Builder<Integer, Drama>(dataSource, myConfig)
                                    .setInitialKey(0)
                                    .build();

                            return pagedDramas;
                        }
                    });
                }else{
                    Log.i(TAG, "從本地讀資料: ");
                    return dramaListRepository.getDatalocal();
                }

            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(dataObserver);




    }

}
