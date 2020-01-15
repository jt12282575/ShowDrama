package dada.com.showdrama.ShowDramaDetail;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import dada.com.showdrama.Base.BasePresenter;
import dada.com.showdrama.Model.Drama;
import dada.com.showdrama.Model.DramaPack;
import dada.com.showdrama.RxRelative.RxUnit;
import dada.com.showdrama.Repositary.DramaRepository;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class DramaDetailPresenter extends BasePresenter<IDramaDetailView> {
    private DramaRepository dramaRepository;
    private static final String TAG = "DramaDetailPresenter";
    private RxUnit rxUnit = new RxUnit();
    private String DATATAG = "datasource";

    public DramaDetailPresenter(IDramaDetailView iView,@NonNull Application application) {
        super(application);
        attachView(iView);
    }
    public void getDrama(Intent intent) {
        Drama drama = (Drama) intent.getSerializableExtra(DramaDetailActivity.DETAIL_DRAMA);
        if(drama!= null ) mvpView.setDrama(drama); // 如果是從外面接來的 Drama 就直接用
        else{ // 從 appLinkIntent 來的 intent
            String action = intent.getAction();
            String data = intent.getDataString();
            if (Intent.ACTION_VIEW.equals(action) && data != null) {
                Log.i(TAG, "getDrama data: "+data);
                String dramaidstr = data.substring(data.lastIndexOf("/") + 1);
                try {
                    if (dramaRepository == null) dramaRepository = new DramaRepository();
                    final int dramaid = Integer.parseInt(dramaidstr);
                    Log.i(TAG, "getDrama: id: "+dramaid);
                    mvpView.showLoading();


                    if (dramaRepository == null) dramaRepository = new DramaRepository();
                    dramaRepository.getDramaFromDb(dramaid) // 檢查 Local Db 是否為空
                            .toObservable()
                            .onErrorResumeNext(
                                    dramaRepository.getDataRemote() //若 Local Db 為空，透過 Retrofit 去取得 data
                                            .retryWhen(rxUnit.doRetry(6, 10000)) //重試 6 次 ， 每次 10 秒
                                            .flatMap(insertDataToDbAndGetDrama()) // 如果成功連線則放資料進DB，並等 500 毫秒
                                            .flatMapSingle(getDramaSingle(dramaid)) // 重新進DB 取得 data
                                            .flatMap(mapBackToObserver())// 重連成功，資料存進 Local
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(getDisableObserver());


                } catch (NumberFormatException e) {
                    Log.i(TAG, "getDrama: 並不是數字");
                    e.printStackTrace();
                    mvpView.noDrama(e.getMessage());
                }

            }



        }




    }

    @NotNull
    private DisposableObserver<Drama> getDisableObserver() {
        DisposableObserver<Drama> disableObserver = new DisposableObserver<Drama>() {
            @Override
            public void onNext(Drama drama) {
                Log.i(DATATAG, "資料庫有資料");
                mvpView.setDrama(drama);
            }

            @Override
            public void onError(Throwable e) {
                mvpView.hideLoading();
                mvpView.noDrama(e.getMessage());
                Log.i(DATATAG, "onError: " + e.getMessage().toString());
            }

            @Override
            public void onComplete() {
                mvpView.hideLoading();
            }
        };
        addSubScribe(disableObserver);
        return disableObserver;
    }

    @NotNull
    private Function<Drama, ObservableSource<Drama>> mapBackToObserver() {
        return new Function<Drama, ObservableSource<Drama>>() {
            @Override
            public ObservableSource<Drama> apply(final Drama drama) throws Exception {
                return new ObservableSource<Drama>() {
                    @Override
                    public void subscribe(Observer<? super Drama> observer) {
                        observer.onNext(drama);
                    }
                };
            }
        };
    }

    @NotNull
    private Function<Drama, SingleSource<Drama>> getDramaSingle(final int dramaid) {
        return new Function<Drama, SingleSource<Drama>>() {
            @Override
            public SingleSource<Drama> apply(Drama drama) throws Exception {
                return dramaRepository.getDramaFromDb(dramaid);
            }
        };
    }

    @NotNull
    private Function<DramaPack, ObservableSource<Drama>> insertDataToDbAndGetDrama() {
        return new Function<DramaPack, ObservableSource<Drama>>() {
            @Override
            public ObservableSource<Drama> apply(DramaPack dramaPack) throws Exception {
                dramaRepository.insertDataInDb(dramaPack.getData());
                Log.i(DATATAG, "存進 DB ");
                return
                        new Observable<Drama>() {
                    @Override
                    protected void subscribeActual(Observer<? super Drama> observer) {
                        observer.onNext(new Drama());
                    }
                }.delay(500, TimeUnit.MILLISECONDS); // 留 500 毫秒等 db insert
            }
        };
    }



}
