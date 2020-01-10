package dada.com.showdrama.ShowDramaList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import java.util.List;

import dada.com.showdrama.Base.MVPActivity;
import dada.com.showdrama.Model.Drama;
import dada.com.showdrama.R;
import io.reactivex.Observable;

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
        rcv_dramalist.setAdapter(dramaAdapter);

    }


    private SearchView.OnQueryTextListener onQueryTextListener =
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    getDealsFromDb(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    getDealsFromDb(newText);
                    return true;
                }

                private void getDealsFromDb(String searchText) {
                    searchText = "%"+searchText+"%";
                    Log.i(TAG, "Search Text: "+searchText);
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
    public String getSearchKeyword() {
        return null;
    }

    @Override
    public boolean checkSearchKeywordNotNull(String searchKeyword) {
        return false;
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


    @Override
    public Observable<Connectivity> getNetworkState() {
        return ReactiveNetwork.observeNetworkConnectivity(this)   ;
    }
}
