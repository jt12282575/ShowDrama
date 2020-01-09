package dada.com.showdrama;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Global extends Application {
    public static Global instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
