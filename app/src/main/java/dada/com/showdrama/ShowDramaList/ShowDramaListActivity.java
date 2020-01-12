package dada.com.showdrama.ShowDramaList;

import androidx.annotation.NonNull;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import dada.com.showdrama.Base.MVPActivity;
import dada.com.showdrama.Model.Drama;
import dada.com.showdrama.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class ShowDramaListActivity extends MVPActivity<DramaListPresenter> implements IDramaListView {
    private static final String TAG = "dramalist_activity";
    private Toolbar toolbar;
    private SearchView searchView;
    private ProgressBar pbLoading;
    private RecyclerView rcv_dramalist;
    private DramaAdapter dramaAdapter;

    @Override
    protected DramaListPresenter createPresenter() {
        return new DramaListPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_drama_list);
        initView();
        presenter.loadData();
        presenter.updateDataFromNet();
    }

    private void initView() {
        toolbar = findViewById(R.id.sdl_toolbar);
        setToolbar("戲劇列表",toolbar);

        pbLoading = findViewById(R.id.sdl_pb_loading);

        searchView =  findViewById(R.id.sdl_sv_searchkeyword);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(onQueryTextListener);

        rcv_dramalist = findViewById(R.id.sdl_rcv_dramalist);
        rcv_dramalist.setHasFixedSize(true);
        rcv_dramalist.setLayoutManager(new LinearLayoutManager(this));
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int imageWidth = (int) (metrics.widthPixels * 0.25);
        Log.i(TAG, "initView: metric get width "+metrics.widthPixels+" imageWidth: "+imageWidth);
        dramaAdapter = new DramaAdapter(new DiffUtil.ItemCallback<Drama>() {
            @Override
            public boolean areItemsTheSame(@NonNull Drama oldItem, @NonNull Drama newItem) {
                return oldItem.getDramaId() == newItem.getDramaId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Drama oldItem, @NonNull Drama newItem) {
                return oldItem.equals(newItem);
            }
        });
        dramaAdapter.setActivity(this);
        dramaAdapter.setImageWidth(imageWidth);
        rcv_dramalist.setAdapter(dramaAdapter);

    }



    private SearchView.OnQueryTextListener onQueryTextListener =
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    presenter.searchDramaFromKeyWord(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    presenter.searchDramaFromKeyWord(newText);
                    return true;
                }


            };


    @Override
    public void getDramasSuccess(PagedList<Drama> dramas) {
        dramaAdapter.submitList(dramas);
    }

    @Override
    public void getDramaFail(String msg) {
        Toast.makeText(this, "網路出現問題，請確認網路狀態", Toast.LENGTH_SHORT).show();
    }



    @Override
    public boolean ifDramaListHaveData() {
        if(dramaAdapter == null){
            return true;
        }
        if(dramaAdapter.getCurrentList().isEmpty()){
            return false;
        }
        return true;
    }

    @Override
    public void showLoading() {
        pbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        pbLoading.setVisibility(View.GONE);
    }

    @Override
    public void showNetworkError() {
        Toast.makeText(this, "目前沒有網路服務，請重新嘗試。", Toast.LENGTH_SHORT).show();
    }




    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
