package dada.com.showdrama.ShowDramaList;

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
import io.reactivex.Observable;
import io.reactivex.Single;

public class DramaListRepository implements BaseContract.IBaseRepositary<Drama> {
    protected static ApiStores apiStores;
    protected  DramaDao dramaDao;


    private static final String TAG = "DramaListRepository";
    private PagedList.Config config;
    private int PAGE_SIZE = Constant.PAGESIZE;
    private int PRE_FETCH_DISTANCE = Constant.PRE_FETCH_DISTANCE;

    public DramaListRepository() {
        dramaDao = getDramaDao();
        config = new PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setInitialLoadSizeHint(PAGE_SIZE)
                .setPrefetchDistance(PRE_FETCH_DISTANCE)
                .setEnablePlaceholders(false)
                .build();
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                dramaDao.deleteAll();
            }
        }).start();*/
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream raw =  Global.instance.getApplicationContext().getResources().openRawResource(R.raw.testdata);
                Reader rd = new BufferedReader(new InputStreamReader(raw));
                Gson gson = new Gson();
                DramaPack obj = gson.fromJson(rd, DramaPack.class);
                dramaDao.deleteAll();
                dramaDao.insertDramasList(obj.getData());
            }
        }).start();*/

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

    public Single<Drama> getDramaFromDb(int did){
        return dramaDao.getDramaById(did);
    }

    @Override
    public Observable<PagedList<Drama>> getDatalocal() {
        DataSource.Factory<Integer,Drama> factory = getDramaDao().getAll();
        RxPagedListBuilder<Integer,Drama> rxPagedListBuilder = new RxPagedListBuilder(factory, config);
        return rxPagedListBuilder.buildObservable();
    }

    public Observable<PagedList<Drama>> getDramaFromKeyWord(String keyword){
        DataSource.Factory<Integer,Drama> factory = getDramaDao().getPartialDramas(keyword);
        RxPagedListBuilder<Integer,Drama> rxPagedListBuilder = new RxPagedListBuilder(factory, config);
        return rxPagedListBuilder.buildObservable();
    }

    public void insertDataInDb(List<Drama> dramas){
        getDramaDao().insertDramasList(dramas);
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
