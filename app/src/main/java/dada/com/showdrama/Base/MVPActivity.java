package dada.com.showdrama.Base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

}
