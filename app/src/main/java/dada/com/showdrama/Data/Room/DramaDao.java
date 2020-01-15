package dada.com.showdrama.Data.Room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import dada.com.showdrama.Model.Drama;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;


@Dao
public interface DramaDao {

    @Query("SELECT * FROM DRAMA")
    public abstract DataSource.Factory<Integer,Drama> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertDramasList(List<Drama> dramas);

    @Query("SELECT * FROM DRAMA")
    public  DataSource.Factory<Integer,Drama> searchDramas();

    @Query("SELECT * FROM DRAMA")
    public  Single<List<Drama>> getAllDrama();

    @Query("SELECT * FROM DRAMA WHERE name LIKE:keyword Order BY dramaId ASC " )
    public Maybe<List<Drama>> searchDramasList(String keyword);

    @Query("SELECT * FROM DRAMA WHERE dramaId=:dramaId")
    public Single<Drama> getDramaById(int dramaId);

    @Query("DELETE FROM DRAMA")
    public void deleteAll();

    @Query("SELECT * FROM DRAMA ORDER BY dramaId LIMIT 1")
    public Single<Drama> checkIfDatabaseEmpty();


}
