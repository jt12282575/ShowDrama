package dada.com.showdrama.ShowDramaDetail;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import dada.com.showdrama.R;
import dada.com.showdrama.ShowDramaList.ShowDramaListActivity;

public class ShowDramaDetailActivity extends AppCompatActivity {
    private ImageView iv_drama;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    String imageUrl = "https://i.pinimg.com/originals/61/d4/be/61d4be8bfc29ab2b6d5cab02f72e8e3b.jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_drama_detail);
        iv_drama = findViewById(R.id.sdd_iv_drama);
        toolbar = findViewById(R.id.sdd_toolbar);
        appBarLayout = findViewById(R.id.sdd_appbar);

        this.setSupportActionBar(toolbar);
        final ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                {
                    //  Collapsed
                    actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
                }
                else
                {
                    //Expanded
                    actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
                }
            }
        });

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Picasso.get().load(imageUrl).resize(metrics.widthPixels,0).placeholder(R.drawable.ic_file_download_green_80dp).into(iv_drama);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(ShowDramaDetailActivity.this,ShowDramaListActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ShowDramaDetailActivity.this,ShowDramaListActivity.class);
        startActivity(intent);

    }
}
