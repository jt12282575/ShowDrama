package dada.com.showdrama.ShowDramaDetail;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import dada.com.showdrama.Base.MVPActivity;
import dada.com.showdrama.Model.Drama;
import dada.com.showdrama.R;
import dada.com.showdrama.ShowDramaList.ShowDramaListActivity;
import dada.com.showdrama.Util.Constant;
import dada.com.showdrama.Util.UtilFunction;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

public class DramaDetailActivity extends MVPActivity<DramaDetailPresenter> implements IDramaDetailView {
    private ImageView iv_drama;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private ProgressBar pbLoading;
    private TextView tvDramaName;
    private TextView tvDramaCreateAt;
    private TextView tvTotalViews;
    private TextView tvRating;
    private MaterialRatingBar materialRatingBar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private String imageUrl = "https://i.pinimg.com/originals/61/d4/be/61d4be8bfc29ab2b6d5cab02f72e8e3b.jpg";
    public static String DETAIL_DRAMA = "detail_drama";

    private static final String TAG = "DramaDetailActivity";

    @Override
    protected DramaDetailPresenter createPresenter() {
        return new DramaDetailPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_drama_detail);
        initView();
        presenter.getDrama(getIntent());

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();



    }

    private void initView() {
        iv_drama = findViewById(R.id.sdd_iv_drama);
        toolbar = findViewById(R.id.sdd_toolbar);
        appBarLayout = findViewById(R.id.sdd_appbar);
        pbLoading = findViewById(R.id.sdd_pb_loading);
        tvTotalViews = findViewById(R.id.sdd_tv_total_view);
        tvDramaCreateAt = findViewById(R.id.sdd_tv_updatingdate);
        tvDramaName = findViewById(R.id.sdd_tv_drama_name);
        tvRating = findViewById(R.id.sdd_tv_rating);
        materialRatingBar = findViewById(R.id.sdd_rb_rating);
        collapsingToolbarLayout = findViewById(R.id.sdd_collapsing);

        materialRatingBar.setEnabled(false);

        this.setSupportActionBar(toolbar);
        final ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    //  Collapsed
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
                } else {
                    actionBar.setDisplayHomeAsUpEnabled(false);
                    //Expanded
//                    actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
                }
            }
        });
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(DramaDetailActivity.this,ShowDramaListActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }*/

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DramaDetailActivity.this,ShowDramaListActivity.class);
        startActivity(intent);

    }*/

    @Override
    public void setDrama(final Drama drama) {
        if(drama!=null){
            tvDramaName.setText(drama.getName());
            tvTotalViews.setText("觀看次數 "+ drama.getTotalViews()+" 次");
            tvDramaCreateAt.setText("上傳時間 "+ UtilFunction.fromISO8601UTC(drama.getCreatedAt()));

            DecimalFormat df=new DecimalFormat("#.#");
            String rating=df.format(drama.getRating());
            tvRating.setText(rating);

            final DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            Picasso.get().load(drama.getThumb())
                    .resize(metrics.widthPixels, 0)
                    .memoryPolicy(NO_CACHE, NO_STORE)
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.drawable.ic_file_download_green_80dp)
                    .into(iv_drama);

            materialRatingBar.setRating(drama.getRating().floatValue());
        }else{
            // 空 Drama
            Log.i(TAG, "setDrama: 空 drama");
        }
    }

    @Override
    public void noDrama(String msg) {
        tvDramaName.setText("找不到該劇目");
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


}
