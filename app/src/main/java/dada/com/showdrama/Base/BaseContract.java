package dada.com.showdrama.Base;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

public interface BaseContract {
    interface IBaseView{
        void showLoading();
        void hideLoading();
    }

    interface IBasePresenter<V extends IBaseView>{
        void attachView(V mvpView);
        void detachView();
        void onUnSubScribe();
        <T> void addSubScribe(Observable<T> observable, DisposableObserver<T> disposableObserver);
    }

    interface IBaseRepositary<T>{
        Observable<List<T>> getDatalocal();
    }




}
