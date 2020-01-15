package dada.com.showdrama.Base;

import android.content.Context;

import androidx.paging.PagedList;

import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;

import java.util.List;

import dada.com.showdrama.Model.DramaPack;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;

public interface BaseContract {
    interface IBaseView{
        void showLoading();
        void hideLoading();
        void showNetworkError();
        Observable<Boolean> getNetworkState();
    }

    interface IBasePresenter<V extends IBaseView>{
        void attachView(V mvpView);
        void detachView();
        void onUnSubScribe();
        <T> void addSubScribe(Observable<T> observable, DisposableObserver<T> disposableObserver);
        <T> void addSubScribe(Single<T> observable, DisposableSingleObserver<T> disposableObserver);
        <T> void addSubScribe(Maybe<T> observable, DisposableMaybeObserver<T> disposableObserver);
        <T> void addSubScribe( DisposableObserver<T> disposableObserver);

    }

    interface IBaseRepositary<T>{

        Observable<DramaPack> getDataRemote();
    }




}
