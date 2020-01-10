package dada.com.showdrama.Base;

import android.content.Context;

import androidx.paging.PagedList;

import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;

import java.util.List;

import dada.com.showdrama.Model.DramaPack;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

public interface BaseContract {
    interface IBaseView{
        void showLoading();
        void hideLoading();
        void showNetworkError();
        Observable<Connectivity> getNetworkState();
    }

    interface IBasePresenter<V extends IBaseView>{
        void attachView(V mvpView);
        void detachView();
        void onUnSubScribe();
        <T> void addSubScribe(Observable<T> observable, DisposableObserver<T> disposableObserver);
        <T> void addSubScribe( DisposableObserver<T> disposableObserver);
    }

    interface IBaseRepositary<T>{
        Observable<PagedList<T>> getDatalocal();
        Observable<DramaPack> getDataRemote();
    }




}
