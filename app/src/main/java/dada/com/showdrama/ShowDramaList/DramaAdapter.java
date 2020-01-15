package dada.com.showdrama.ShowDramaList;

import android.app.Activity;
import android.content.Context;
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
import java.util.List;

import dada.com.showdrama.Model.Drama;
import dada.com.showdrama.R;
import dada.com.showdrama.ShowDramaDetail.DramaDetailActivity;
import dada.com.showdrama.Util.UtilFunction;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class DramaAdapter extends RecyclerView.Adapter<DramaAdapter.DramaViewHolder> {
    public  int imageWidth;
    public Activity activity;
    private ItemClick itemClick;
    private Object picasso_tag;
    private List<Drama> dramaList;
    private Context context;



    public DramaAdapter(Context context,Activity activity, ItemClick itemClick, Object picasso_tag, List<Drama> dramaList,int imageWidth) {
        this.activity = activity;
        this.itemClick = itemClick;
        this.picasso_tag = picasso_tag;
        this.dramaList = dramaList;
        this.imageWidth = imageWidth;
        this.context = context;
    }



    @NonNull
    @Override
    public DramaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.rcv_drama, parent, false);
        return new DramaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final DramaViewHolder holder, final int position) {
         final Drama drama = dramaList.get(position);
        holder.setName(drama.getName());
        holder.setId(Integer.toString(drama.getDramaId()));
        holder.setDate(drama.getCreatedAt());
        holder.setDramaRating(drama.getRating());
        holder.setIvThumb(drama.getThumb());
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
    public void setDramaList(List<Drama> dramaList){
        this.dramaList = dramaList;
    }

    @Override
    public int getItemCount() {
        return dramaList.size();
    }

    class DramaViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDramaName;
        private TextView tvDramaRating;
        private MaterialRatingBar materialRatingBar;
        private TextView tvUploadDate;
        private ImageView ivThumb;
        private TextView tvId;
        private View itemView;

        public DramaViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvDramaName = (TextView) itemView.findViewById(R.id.rcv_drama_tv_name);
            tvDramaRating = (TextView) itemView.findViewById(R.id.rcv_drama_tv_rating);
            tvUploadDate = (TextView) itemView.findViewById(R.id.rcv_drama_tv_updatingdate);
            ivThumb = (ImageView) itemView.findViewById(R.id.rcv_drama_iv_thumb);
            materialRatingBar = (MaterialRatingBar) itemView.findViewById(R.id.rcv_drama_rb_rating);
            tvId = (TextView) itemView.findViewById(R.id.rcv_drama_tv_showid);

        }

        public void setName(String name){
            tvDramaName.setText(name);
        }

        public void setId(String id){
            tvId.setText("劇 id "+id);
        }

        public void setIvThumb(final String thumb){
            Picasso.get()
                    .load(thumb)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .resize(imageWidth,0)
                    .tag(picasso_tag)
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.drawable.ic_file_download_green_80dp).into(ivThumb ,new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(thumb)
                            .resize(imageWidth, 0)
                            .tag(picasso_tag)
                            .config(Bitmap.Config.RGB_565)
                            .into(ivThumb);
                }
            });
        }

        public void setDramaRating(Double rating){
            DecimalFormat df=new DecimalFormat("#.#");
            String ratingStr=df.format(rating);
            tvDramaRating.setText(ratingStr);
            materialRatingBar.setRating(rating.floatValue());
            materialRatingBar.setEnabled(false);
        }

        public void setDate(String date){
            tvUploadDate.setText("上傳日期 "+UtilFunction.fromISO8601UTC(date));
        }







    }



    interface ItemClick{
        void onItemClick(View v, int position);
    }


}
