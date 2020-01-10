package dada.com.showdrama.Base;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public abstract class BasePresenter<V extends BaseContract.IBaseView> implements BaseContract.IBasePresenter<V> {
    protected V mvpView;
    private CompositeDisposable mCompositeDisposable;

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
