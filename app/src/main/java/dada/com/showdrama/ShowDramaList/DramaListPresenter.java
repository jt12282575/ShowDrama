package dada.com.showdrama.ShowDramaList;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dada.com.showdrama.Base.BasePresenter;

import dada.com.showdrama.Data.Retrofit.ApiCallback;
import dada.com.showdrama.Global;
import dada.com.showdrama.Model.Drama;
import dada.com.showdrama.Model.DramaPack;
import dada.com.showdrama.R;
import dada.com.showdrama.Repositary.DramaRepository;
import dada.com.showdrama.RxRelative.RxUnit;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DramaListPresenter extends BasePresenter<IDramaListView> {
    private DramaRepository dramaRepository;
    private RxUnit rxUnit = new RxUnit();


    public DramaListPresenter(IDramaListView iView, @NonNull Application application) {
        super(application);
        attachView(iView);
    }

    private static final String TAG = "dramalist_presenter";
    private static final String DATATAG = "datasource";



    private int retryCount = 0;

    public void cleanDb() {
        addSubScribe(Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                dramaRepository.cleanDb();
                emitter.onNext(true);
            }
        }).delay(500,TimeUnit.MILLISECONDS), new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                mvpView.updateDramaList(new ArrayList<Drama>());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }



    public void initData() {
        if (dramaRepository == null) dramaRepository = new DramaRepository();
        dramaRepository.checkIfDbHasDrama() // 檢查 Local Db 是否為空
                .toObservable()
                .onErrorResumeNext(
                        dramaRepository.getDataRemote() //若 Local Db 為空，透過 Retrofit 去取得 data
                                .retryWhen(rxUnit.doRetry(6, 5000)) // 如果 Retrofit 取 data 失敗，做重連
                                .flatMap(insertDataToDb()) // 重連成功，資料存進 Local
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getDisableObserver());


    }

    @NotNull
    private DisposableObserver<Drama> getDisableObserver() {
        return new DisposableObserver<Drama>() {
            @Override
            public void onNext(Drama drama) {
                Log.i(DATATAG, "資料庫有資料");
                loadDramaData();
            }

            @Override
            public void onError(Throwable e) {
                Log.i(DATATAG, "onError: " + e.getMessage().toString());
            }

            @Override
            public void onComplete() {
                mvpView.hideLoading();
            }
        };
    }


    @NotNull
    private Function<DramaPack, ObservableSource<Drama>> insertDataToDb() {
        return new Function<DramaPack, ObservableSource<Drama>>() {
            @Override
            public ObservableSource<Drama> apply(DramaPack dramaPack) throws Exception {
                dramaRepository.insertDataInDb(dramaPack.getData());
                Log.i(DATATAG, "存進 DB ");
                return new Observable<Drama>() {
                    @Override
                    protected void subscribeActual(Observer<? super Drama> observer) {
                        observer.onNext(new Drama());
                    }
                }.delay(500, TimeUnit.MILLISECONDS); // 留 500 毫秒等 db insert
            }
        };
    }



    public void searchDramaFromKeyWord(String searchkey) {
        Log.i(DATATAG, "從關鍵字搜尋: ");

        if (dramaRepository == null) dramaRepository = new DramaRepository();
        mvpView.showLoading();

        if (!searchkey.equals("")) {
            DisposableMaybeObserver dataObserver = new DisposableMaybeObserver<List<Drama>>() {
                @Override
                public void onSuccess(List<Drama>  dramas) {
                    mvpView.updateDramaList(dramas);
                    mvpView.hideLoading();
                }

                @Override
                public void onError(Throwable e) {
                    mvpView.hideLoading();
                    mvpView.getDramaFail(e.getMessage());
                }

                @Override
                public void onComplete() {
                    mvpView.hideLoading();
                }
            };
            String searchKeyInDb = getSearchKeyword(searchkey);
            addSubScribe(dramaRepository.getDramaListFromKeyWord(searchKeyInDb), dataObserver);
        }else{
            DisposableSingleObserver dataObserverSingle = new DisposableSingleObserver<List<Drama>>() {
                @Override
                public void onSuccess(List<Drama>  dramas) {
                    Log.i(DATATAG, "all data size "+dramas.size());
                    for (int i = 0; i < dramas.size(); i++) {
                        Log.i(DATATAG, "id: "+dramas.get(i).getDramaId()+" name: "+dramas.get(i).getName());
                    }

                    mvpView.updateDramaList(dramas);
                    mvpView.hideLoading();
                }

                @Override
                public void onError(Throwable e) {
                    mvpView.hideLoading();
                    mvpView.getDramaFail(e.getMessage());
                }


            };
            addSubScribe(dramaRepository.getAllDrama(), dataObserverSingle);
        }

    }


    public String getSearchKeyword(String searchText) {
        searchText = "%" + searchText + "%";
        return searchText;
    }



    public void updateDataFromNet() {
        addSubScribe(dramaRepository.getDataRemote().doOnNext(new Consumer<DramaPack>() {
            @Override
            public void accept(DramaPack dramaPack) throws Exception {
                Log.i(TAG, "accept: 先存進 db");
                dramaRepository.insertDataInDb(dramaPack.getData());
            }
        }).delay(500,TimeUnit.MILLISECONDS), new ApiCallback<DramaPack>() {
            @Override
            public void onSuccess(DramaPack dramaPack) {
                Log.i(DATATAG, "onSuccess: json");
                for (int i = 0; i < dramaPack.getData().size(); i++) {
                    Log.i(DATATAG, "drama id: "+dramaPack.getData().get(i).getDramaId());
                }

                searchDramaFromKeyWord(mvpView.getCurrentQuery());
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



    public void loadDramaData() {
        String lastQuery = mvpView.getLastTimeQuery();
        mvpView.setLastQuery(lastQuery);
        Log.i(DATATAG, "loadDramaData: ");

    }


    public void addDataInToDb(){
        if (dramaRepository == null) dramaRepository = new DramaRepository();
        InputStream raw =  Global.instance.getApplicationContext().getResources().openRawResource(R.raw.testdata);
        Reader rd = new BufferedReader(new InputStreamReader(raw));
        Gson gson = new Gson();
        DramaPack dramaPack = gson.fromJson(rd, DramaPack.class);
        List<Drama> dramaList = dramaPack.getData();
        Log.i(DATATAG, "data size: "+dramaList.size());
        final List<Drama> fakeList = new ArrayList<>();
        fakeList.clear();
        for (int i = 13; i <10000 ; i++) {
            Drama fakeDrama = new Drama();
            Drama drama = dramaList.get(i%6);
            fakeDrama.setDramaId(i);
            fakeDrama.setName(i+"劇");
            fakeDrama.setCreatedAt(drama.getCreatedAt());
            fakeDrama.setThumb(drama.getThumb());
            fakeDrama.setTotalViews(drama.getTotalViews());
            fakeDrama.setRating(drama.getRating());
            Log.i(DATATAG, "addDataInToDb: "+i+"  get"+(i%6));
            fakeList.add(fakeDrama);
        }
        Log.i(DATATAG, "addDataInToDb: size "+ fakeList.size());
        new Thread(new Runnable() {
            @Override
            public void run() {
                dramaRepository.dramaDao.insertDramasList(fakeList);
            }
        }).start();
    }





}
