package dada.com.showdrama.Base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public abstract class BasePresenter<V extends BaseContract.IBaseView> extends AndroidViewModel implements BaseContract.IBasePresenter<V> {
    protected V mvpView;
    private CompositeDisposable mCompositeDisposable;

    public BasePresenter(@NonNull Application application) {
        super(application);
    }

    @Override
    public void onUnSubScribe() {
        if(mCompositeDisposable!= null)
            mCompositeDisposable.dispose();
    }

    @Override
    public <T> void addSubScribe(Observable<T> observable, DisposableObserver<T> disposableObserver) {
        if(mCompositeDisposable == null) mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(disposableObserver);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableObserver);

    }

    @Override
    public <T> void addSubScribe(Maybe<T> observable, DisposableMaybeObserver<T> disposableObserver) {
        if(mCompositeDisposable == null) mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(disposableObserver);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableObserver);

    }

    @Override
    public <T> void addSubScribe(Single<T> single, DisposableSingleObserver<T> disposableSingleObserver) {
        if(mCompositeDisposable == null) mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(disposableSingleObserver);
        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableSingleObserver);

    }

    @Override
    public <T> void addSubScribe(DisposableObserver<T> disposableObserver) {
        if(mCompositeDisposable == null) mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(disposableObserver);
    }

    public void attachView(V mvpView) {
        this.mvpView =  mvpView;
    }


    public void detachView(){
        this.mvpView = null;
        onUnSubScribe();
    }







}
