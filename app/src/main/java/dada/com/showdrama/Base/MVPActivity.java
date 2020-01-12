package dada.com.showdrama.Base;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import dada.com.showdrama.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public abstract class MVPActivity<P extends BasePresenter> extends AppCompatActivity {
    protected P presenter;
    protected abstract P createPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = createPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(presenter != null) presenter.detachView();
    }

    public void setToolbar(String title,Toolbar toolbar){
        if (toolbar != null) {
            toolbar.setTitle(title);
            setSupportActionBar(toolbar);
        }
    }


    public Observable<Boolean> getNetworkState() {

//        return ReactiveNetwork.observeNetworkConnectivity(this)   ;
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                try {
                    int timeoutMs = 1500;
                    Socket sock = new Socket();
                    SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

                    sock.connect(sockaddr, timeoutMs);
                    sock.close();
                    Log.i("baseac", "subscribe: 有連線");
                    emitter.onNext(true);
                } catch (IOException e) {
                    Log.i("baseac", "subscribe: 沒有連線");
                    emitter.onNext(false);
                }
            }
        });
    }

}
