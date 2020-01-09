package dada.com.showdrama.Data;

import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

public abstract class ApiCallback<M> extends DisposableObserver<M> {
    //Success,Fail,Finish
    public abstract void onSuccess(M model);

    public abstract void onFail(String msg);

    public abstract void onFinish();

    @Override
    public void onNext(M m) {
        onSuccess(m);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            int code = httpException.code();
            String msg = httpException.getMessage();
            if (code == 504) {
                msg = "網路不穩定";
            }
            if (code == 502 || code == 404) {
                msg = "連線異常請稍後再試";
            }
            onFail(msg);
        }else{
            onFail(e.getMessage());
        }
        onFinish();
    }

    @Override
    public void onComplete() {
        onFinish();
    }
}
