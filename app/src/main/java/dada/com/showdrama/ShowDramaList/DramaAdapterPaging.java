package dada.com.showdrama.ShowDramaList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import dada.com.showdrama.Model.Drama;
import dada.com.showdrama.R;
import dada.com.showdrama.ShowDramaDetail.DramaDetailActivity;
import dada.com.showdrama.Util.UtilFunction;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class DramaAdapterPaging extends PagedListAdapter<Drama, DramaAdapterPaging.DramaViewHolder> {
    public static int imageWidth;
    public Activity activity;
    private ItemClick itemClick;
    private Object picasso_tag;

    protected DramaAdapterPaging(@NonNull DiffUtil.ItemCallback<Drama> diffCallback, Activity activity, ItemClick itemClick, Object picasso_tag) {
        super(diffCallback);
        this.activity = activity;
        this.itemClick = itemClick;
        this.picasso_tag = picasso_tag;
    }



    protected DramaAdapterPaging(@NonNull AsyncDifferConfig<Drama> config, Activity activity, ItemClick itemClick, Object picasso_tag) {
        super(config);
        this.activity = activity;
        this.itemClick = itemClick;
        this.picasso_tag = picasso_tag;
    }



    public void setImageWidth(int imageWidth){
        this.imageWidth = imageWidth;
    }

    @NonNull
    @Override
    public DramaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_drama, parent, false);
        return new DramaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final DramaViewHolder holder, final int position) {
        final Drama drama = getItem(position);
        holder.tvDramaName.setText(drama.getName());
        DecimalFormat df=new DecimalFormat("#.#");
        String rating=df.format(drama.getRating());
        holder.tvDramaRating.setText(rating);
        holder.materialRatingBar.setRating(drama.getRating().floatValue());
        holder.materialRatingBar.setEnabled(false);
        holder.tvId.setText("劇 id: "+drama.getDramaId());
        Picasso.get()

                .load(drama.getThumb())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .resize(DramaAdapterPaging.imageWidth,0)
                .tag(picasso_tag)
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.drawable.ic_file_download_green_80dp).into(holder.ivThumb ,new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(drama.getThumb())
                        .resize(DramaAdapterPaging.imageWidth, 0)
                        .tag(picasso_tag)
                        .config(Bitmap.Config.RGB_565)
                        .into(holder.ivThumb);
            }
        });
        holder.tvUploadDate.setText("上傳日期 "+UtilFunction.fromISO8601UTC(drama.getCreatedAt()));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick.onItemClick(v,position);
                Intent mIntent = new Intent(holder.itemView.getContext(), DramaDetailActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(DramaDetailActivity.DETAIL_DRAMA, drama);
                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity
                        , new Pair<View,String>(holder.ivThumb, "trans_thumb")
                );
                mIntent.putExtras(mBundle);
                holder.itemView.getContext().startActivity(mIntent,activityOptions.toBundle());
            }
        });

    }

    public  class DramaViewHolder extends RecyclerView.ViewHolder {

        TextView tvDramaName;
        TextView tvDramaRating;
        MaterialRatingBar materialRatingBar;
        TextView tvUploadDate;
        ImageView ivThumb;
        TextView tvId;


        public DramaViewHolder(View itemView) {
            super(itemView);
            tvDramaName = (TextView) itemView.findViewById(R.id.rcv_drama_tv_name);
            tvDramaRating = (TextView) itemView.findViewById(R.id.rcv_drama_tv_rating);
            tvUploadDate = (TextView) itemView.findViewById(R.id.rcv_drama_tv_updatingdate);
            ivThumb = (ImageView) itemView.findViewById(R.id.rcv_drama_iv_thumb);
            materialRatingBar = (MaterialRatingBar) itemView.findViewById(R.id.rcv_drama_rb_rating);
            tvId = (TextView) itemView.findViewById(R.id.rcv_drama_tv_showid);
        }


    }

    interface ItemClick{
        void onItemClick(View v, int position);
    }


}
