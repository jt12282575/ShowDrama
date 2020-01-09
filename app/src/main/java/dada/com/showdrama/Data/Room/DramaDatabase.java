package dada.com.showdrama.Data.Room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import dada.com.showdrama.Model.Drama;
import dada.com.showdrama.Model.DramaPack;
import dada.com.showdrama.R;

@Database(entities = {Drama.class}, version = 1)
public abstract class DramaDatabase extends RoomDatabase {
    private static final String DB_NAME = "drama.db";
    private static DramaDatabase INSTANCE;


    public static DramaDatabase getDramaDatabase(final Context context) {


        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, DramaDatabase.class, DB_NAME).build();

        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }



    public abstract DramaDao dramaDao();
}