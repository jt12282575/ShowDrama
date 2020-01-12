package dada.com.showdrama.Data.Room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import dada.com.showdrama.Model.Drama;
import io.reactivex.Observable;


@Dao
public interface DramaDao {

    @Query("SELECT * FROM DRAMA")
    public abstract DataSource.Factory<Integer,Drama> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertDramasList(List<Drama> dramas);

    @Query("SELECT * FROM DRAMA")
    public abstract DataSource.Factory<Integer,Drama> getPartialDramas();

    @Query("SELECT * FROM DRAMA WHERE name LIKE:keyword")
    public abstract DataSource.Factory<Integer,Drama> getPartialDramas(String keyword);

    @Query("SELECT * FROM DRAMA WHERE dramaId=:dramaId")
    public Observable<Drama> getDramaById(String dramaId);

    @Query("DELETE FROM DRAMA")
    public void deleteAll();


}
