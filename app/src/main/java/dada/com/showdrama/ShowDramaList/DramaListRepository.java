package dada.com.showdrama.ShowDramaList;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.paging.RxPagedListBuilder;


import java.util.List;

import dada.com.showdrama.Base.BaseContract;
import dada.com.showdrama.Data.Retrofit.ApiClient;
import dada.com.showdrama.Data.Retrofit.ApiStores;
import dada.com.showdrama.Data.Room.DramaDao;
import dada.com.showdrama.Data.Room.DramaDatabase;
import dada.com.showdrama.Global;
import dada.com.showdrama.Model.Drama;
import dada.com.showdrama.Model.DramaPack;
import dada.com.showdrama.Util.Constant;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public class DramaListRepository implements BaseContract.IBaseRepositary<Drama> {
    protected static ApiStores apiStores;
    protected  DramaDao dramaDao;


    private static final String TAG = "DramaListRepository";
    private static final String DATATAG = "datasource";
    private PagedList.Config config;
    private int PAGE_SIZE = 12;
    private int PRE_FETCH_DISTANCE = 3;

    public DramaListRepository() {
        dramaDao = getDramaDao();
        config = new PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setPrefetchDistance(PRE_FETCH_DISTANCE)
                .build();


    }





    public Single<Drama> getDramaFromDb(int did){
        return dramaDao.getDramaById(did);
    }

    public Single<Drama> checkIfDbHasDrama(){
        Log.i(TAG, "checkIfDbHasDrama: 目前 Thread " +Thread.currentThread().getName());
        return dramaDao.checkIfDatabaseEmpty();
    }

    public void cleanDb(){
        getDramaDao().deleteAll();
    }





    public Maybe<List<Drama>> getDramaListFromKeyWord(String keyword){
        return getDramaDao().searchDramasList(keyword);
    }

    public Single<List<Drama>> getAllDrama(){
        return getDramaDao().getAllDrama();
    }


    public void insertDataInDb(List<Drama> dramas){
        getDramaDao().insertDramasList(dramas);
    }


    public Observable<DramaPack> getDataRemote() {

        Log.i(DATATAG, "getDataRemote: 從網路取資料");
        Log.i(DATATAG, "目前thread: "+Thread.currentThread().getName());
        return apiStores().getDramaPack();
    }

    public void storeDramaInDb(final List<Drama> dramas){
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                getDramaDao().insertDramasList(dramas);
            }
        });
    }
    
    public  DramaDao getDramaDao(){
        if (dramaDao == null){
            dramaDao = DramaDatabase.getDramaDatabase(Global.instance.getApplicationContext()).dramaDao();
        }
        return dramaDao;
    }

    public static ApiStores apiStores(){
        if (apiStores == null) apiStores = ApiClient.retrofit().create(ApiStores.class);
        return apiStores;
    }




}
