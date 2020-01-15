package dada.com.showdrama.ShowDramaList;

import androidx.annotation.NonNull;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dada.com.showdrama.Base.MVPActivity;
import dada.com.showdrama.Model.Drama;
import dada.com.showdrama.R;
import dada.com.showdrama.Util.Constant;

public class ShowDramaListActivity extends MVPActivity<DramaListPresenter> implements IDramaListView {
    private static final String TAG = "dramalist_activity";
    private Toolbar toolbar;
    private SearchView searchView;
    private SearchView.SearchAutoComplete searchAutoComplete;
    private ProgressBar pbLoading;
    private RecyclerView rcv_dramalist;

    private RelativeLayout rl_root;
    private SwipeRefreshLayout srl_rcv_root;

    private String DRAMA_LIST = "drama_list";
    private String LAST_QUERY = "last_query";
    private String LAST_HISTORY = "last_history";

    private String DATATAG = "datasource";

    private SimpleCursorAdapter suggestionAdapter;
    private List<String> historyWords = new ArrayList<String>();
    private Set<String> historySet = new HashSet<String>();
    private List<Drama> dramaList = new ArrayList<>();
    private DramaAdapter dramaAdapter;

    private static final String[] SUGGESTIONS = {
            "獅子", "黑", "妹妹",
            "可愛", "街到", "維持",
            "可惜", "終於"
    };
    private MatrixCursor cursor;
    private Object picasso_tag;
    private LinearLayoutManager linearLayoutManager;
    private boolean isUserScrolling = false;




    @Override
    protected DramaListPresenter createPresenter() {
        return new DramaListPresenter(this,getApplication());
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_drama_list);
        initView();
        presenter.initData();
    }

    private void initView() {
        toolbar = findViewById(R.id.sdl_toolbar);
        rl_root = findViewById(R.id.sdl_rl_root);
        pbLoading = findViewById(R.id.sdl_pb_loading);
        searchView =  findViewById(R.id.sdl_sv_searchkeyword);
        searchAutoComplete  = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        srl_rcv_root = findViewById(R.id.sdl_srl_rcvroot);



        setToolbar("戲劇列表",toolbar);

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(onQueryTextListener);

        searchAutoComplete.setThreshold(0);

        rcv_dramalist = findViewById(R.id.sdl_rcv_dramalist);
        rcv_dramalist.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        rcv_dramalist.setLayoutManager(linearLayoutManager);
        DisplayMetrics metrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int imageWidth = (int) (metrics.widthPixels * 0.25);

        picasso_tag = new Object();





        dramaAdapter = new DramaAdapter(this, this
                , new DramaAdapter.ItemClick() {
            @Override
            public void onItemClick(View v, int position) {
                hideKeyboard();
                Log.i(TAG, "onItemClick: 按下列表");
                addHistoryWords();
            }
        }, picasso_tag,dramaList,imageWidth);

        rcv_dramalist.setAdapter(dramaAdapter);
        rcv_dramalist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        hideKeyboard();
                        Picasso.get().pauseTag(picasso_tag);
                        isUserScrolling = true;
                        // 手指触屏拉动准备滚动，只触发一次        顺序: 2
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        // 持续滚动开始，只触发一次                顺序: 4
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        Picasso.get().resumeTag(picasso_tag);
                        isUserScrolling = false;
                        // 整个滚动事件结束，只触发一次            顺序: 6
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
        srl_rcv_root.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.updateDataFromNet();
            }
        });

    }



    private SearchView.OnQueryTextListener onQueryTextListener =
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

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

//        dramaAdapterPaging.submitList(dramas);
    }

    @Override
    public void getDramaFail(String msg) {
        Toast.makeText(this, "網路出現問題，請確認網路狀態", Toast.LENGTH_SHORT).show();
    }





    @Override
    protected void onResume() {
        super.onResume();
        getLastTimeHistory();
        setSearchViewSuggestionAdapter();
        rl_root.requestFocus();
    }

    private void setSearchViewSuggestionAdapter() {
        Log.i(TAG, "創建 suggester: ");
         String[] from = new String[] {"keywordString"};
         int[] to = new int[] {android.R.id.text1};
        String[] columnNames = {"_id","keywordString"};
        cursor = new MatrixCursor(columnNames);
        cursor.moveToPosition(-1);
        String[] temp = new String[2];
        int id = 0;

        for(String item : historyWords){
            temp[0] = Integer.toString(id++);
            temp[1] = item;
            cursor.addRow(temp);
        }

        suggestionAdapter = new SimpleCursorAdapter(this,
                R.layout.simple_list_item_1,
                cursor,
                from,
                to,
                CursorAdapter.IGNORE_ITEM_VIEW_TYPE);
        if(searchView != null) {
            searchView.setSuggestionsAdapter(suggestionAdapter);
            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionSelect(int position) {
                    return true;
                }

                @Override
                public boolean onSuggestionClick(int position) {
                    cursor.moveToPosition(position);
                    String suggestion =cursor.getString(1);
                    searchView.setQuery(suggestion,false);
                    return false;
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        saveEndStateAndHistory();

        if(cursor != null){
            cursor.close();
            cursor = null;
        }
    }

    @Override
    public String getCurrentQuery() {
        return searchView.getQuery().toString();
    }



    @Override
    public String getLastTimeQuery() {
        String lastQuery = getSharedPreferences(DRAMA_LIST, MODE_PRIVATE)
                .getString(LAST_QUERY, "");

        Log.i(TAG, "getLastTimeQuery: "+lastQuery);
        return lastQuery;
    }

    @Override
    public void getLastTimeHistory() {
        String lastHistoryJson = getSharedPreferences(DRAMA_LIST, MODE_PRIVATE)
                .getString(LAST_HISTORY, "");
        Log.i(TAG, "last history: "+lastHistoryJson);
        if (!lastHistoryJson.equals("")){
            Gson gson = new Gson();
            try {
                historyWords = gson.fromJson(lastHistoryJson, new TypeToken<List<String>>(){}.getType());
                if (historyWords.size()>0){
                    for (int i = 0; i < historyWords.size(); i++) {
                        historySet.add(historyWords.get(i));
                    }
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }

        }

    }


    @Override
    public void saveEndStateAndHistory() {
        Log.i(TAG, "saveEndStateAndHistory: "+getCurrentQuery());
        SharedPreferences pref = getSharedPreferences(DRAMA_LIST, MODE_PRIVATE);
        if (historyWords.size()>0) {
            Gson gson = new Gson();
            String historyListStr = gson.toJson(historyWords);
            Log.i(TAG, "save last history: "+historyListStr);
            pref.edit().putString(LAST_QUERY, getCurrentQuery())
                    .putString(LAST_HISTORY,historyListStr).commit();
        }else{
            Log.i(TAG, "saveEndStateAndHistory: historyWords 沒資料");
            pref.edit().putString(LAST_QUERY, getCurrentQuery()).commit();
        }

    }

    @Override
    public void setLastQuery(String lastQuery) {
        if (!lastQuery.equals("")) searchView.setIconified(false);
        searchView.setQuery(lastQuery,false);


    }

    @Override
    public void hideKeyboard() {
        View currentview = getCurrentFocus();
        if (currentview != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(currentview.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void finishRefresh() {
        srl_rcv_root.setRefreshing(false);
    }

    @Override
    public void addHistoryWords() {
        // 重作 cursor
        // size 保持在 10筆
        if (!inHistory(getCurrentQuery()) && !getCurrentQuery().equals("")) {

            if (historyWords.size()>= Constant.HISTORYSIZE) {
                String removeWords = historyWords.remove(0);
                historySet.remove(removeWords);
                Log.i(TAG, "addHistoryWords: 刪除 "+removeWords);
            }
            historyWords.add(getCurrentQuery());
            historySet.add(getCurrentQuery());
            Log.i(TAG, "加入 "+getCurrentQuery());

        }
    }


    @Override
    public synchronized void updateDramaList(List<Drama> dramaList) {
        Log.i(DATATAG, "Activity: 更新 size "+dramaList.size());
        for (int i = 0; i < dramaList.size(); i++) {
            Log.i(DATATAG, "id: "+dramaList.get(i).getDramaId()+" name: "+dramaList.get(i).getName());
        }

        dramaAdapter.setDramaList(dramaList);
        dramaAdapter.notifyDataSetChanged();
        rcv_dramalist.invalidate();
    }




    private boolean inHistory(String historyWords) {
        return historySet.contains(historyWords);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_for_test,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id){
            case R.id.menu_setting:
                SharedPreferences pref = getSharedPreferences(DRAMA_LIST, MODE_PRIVATE);
                pref.edit().putString(LAST_HISTORY,"").commit();
                historySet.clear();
                historyWords.clear();
                break;
            case R.id.menu_cleandb:
                presenter.cleanDb();
                break;
            case R.id.menu_db_add:
                presenter.addDataInToDb();
                break;
        }

        return true;
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
