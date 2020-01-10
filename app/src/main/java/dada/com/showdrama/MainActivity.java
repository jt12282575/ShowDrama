package dada.com.showdrama;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import dada.com.showdrama.ShowDramaList.DramaListRepository;
import dada.com.showdrama.Model.Drama;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "test_data";
    DramaListRepository dramaListRepository;
    Button btn_test;
    TextView tv_test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dramaListRepository = new DramaListRepository();
        btn_test = findViewById(R.id.btn_test);
        tv_test = findViewById(R.id.tv_test);
        /*InputStream raw =  getResources().openRawResource(R.raw.testdata);
        Reader rd = new BufferedReader(new InputStreamReader(raw));
        Gson gson = new Gson();
        DramaPack obj = gson.fromJson(rd, DramaPack.class);
        Log.i(TAG, "gson size: "+Integer.toString(obj.getData().size()));*/
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dramaListRepository.getDatalocal().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Drama>>() {
                            @Override
                            public void accept(List<Drama> dramas) throws Exception {
                                Log.i(TAG, "accept: size:"+ Integer.toString(dramas.size()));
                            }
                        });
            }
        });
    }
}
