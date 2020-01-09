package dada.com.showdrama.Base;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public abstract class BasePresenter<V extends BaseContract.IBaseView> {
    protected V mvpView;
    private CompositeDisposable mCompositeDisposable;

    public void attachView(V mvpView){
        this.mvpView = mvpView;
    }
    public void detachView(){
        this.mvpView = null;
        onUnSubscribe();
    }

    public void onUnSubscribe() {
        if(mCompositeDisposable!= null)
            mCompositeDisposable.dispose();
    }

    public <T> void addSubscribe(Observable<T> observable, DisposableObserver<T> disposableObserver){
        if(mCompositeDisposable == null) mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(disposableObserver);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableObserver);
    }




}
