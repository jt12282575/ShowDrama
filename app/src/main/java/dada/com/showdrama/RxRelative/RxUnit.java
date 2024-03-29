package dada.com.showdrama.RxRelative;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class RxUnit {
    @NotNull
    public Function<Observable<Throwable>, ObservableSource<?>> doRetry(final int maxRetries, final int retryDelayMillis) {
        return new Function<Observable<Throwable>, ObservableSource<?>>() {
            private int retryCount = 0;
            private String DATATAG = "datasource";
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                this.retryCount = retryCount;
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        if (++retryCount <= maxRetries) {
                            // When this Observable calls onNext, the original Observable will be retried (i.e. re-subscribed).
                            Log.i(DATATAG, "get error, it will try after " + retryDelayMillis
                                    + " millisecond, retry count " + retryCount);
                            return Observable.timer(retryDelayMillis,
                                    TimeUnit.MILLISECONDS);
                        }
                        // Max retries hit. Just pass the error along.
                        return Observable.error(throwable);
                    }
                });
            }
        };
    }

}
