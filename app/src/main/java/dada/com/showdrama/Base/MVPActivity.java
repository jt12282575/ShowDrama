package dada.com.showdrama.Base;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import dada.com.showdrama.R;

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

}
