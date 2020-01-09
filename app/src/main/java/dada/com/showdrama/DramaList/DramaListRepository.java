package dada.com.showdrama.DramaList;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.paging.RxPagedListBuilder;
import androidx.room.Room;


import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import dada.com.showdrama.Base.BaseContract;
import dada.com.showdrama.Data.ApiClient;
import dada.com.showdrama.Data.ApiStores;
import dada.com.showdrama.Data.Room.DramaDao;
import dada.com.showdrama.Data.Room.DramaDatabase;
import dada.com.showdrama.Global;
import dada.com.showdrama.Model.Drama;
import dada.com.showdrama.Model.DramaPack;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;

public class DramaListRepository implements BaseContract.IBaseRepositary<Drama> {
    protected static ApiStores apiStores;
    protected  DramaDao dramaDao;
    private Context context;

    private static final String TAG = "DramaListRepository";
    private PagedList.Config config;
    private int PAGE_SIZE = 8;
    private int PRE_FETCH_DISTANCE = 3;

    public DramaListRepository() {
        dramaDao = getDramaDao();
        config = new PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setInitialLoadSizeHint(PAGE_SIZE)
                .setPrefetchDistance(PRE_FETCH_DISTANCE)
                .setEnablePlaceholders(false)
                .build();
        final List<Drama> dramaList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            dramaList.add(getDrama(i));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                dramaDao.insertDramasList(dramaList);
            }
        }).start();

    }

    public  Drama getDrama(int did){
        Drama drama = new Drama();
        drama.setDramaId(did);
        drama.setName("dd");
        drama.setThumb("pic");
        drama.setCreatedAt("at");
        drama.setRating(0.5);
        drama.setTotalViews(50);
        return drama;
    }

    @Override
    public Observable<List<Drama>> getDatalocal() {
        return getDramaDao().getAll();
    }

    public Observable<PagedList<Drama>> getDramaFromKeyWord(String keyword){
        DataSource.Factory<Integer,Drama> factory = getDramaDao().getPartialDramas(keyword);
        RxPagedListBuilder<Integer,Drama> rxPagedListBuilder = new RxPagedListBuilder(factory, config);
        return rxPagedListBuilder.buildObservable();
    }


    public Observable<DramaPack> getDataRemote() {
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
