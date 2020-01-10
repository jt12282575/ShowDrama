package dada.com.showdrama.ShowDramaList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import dada.com.showdrama.Model.Drama;
import dada.com.showdrama.R;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class DramaAdapter extends PagedListAdapter<Drama, DramaAdapter.DramaViewHolder> {

    protected DramaAdapter(@NonNull DiffUtil.ItemCallback<Drama> diffCallback) {
        super(diffCallback);
    }

    protected DramaAdapter(@NonNull AsyncDifferConfig<Drama> config) {
        super(config);
    }

    @NonNull
    @Override
    public DramaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_drama, parent, false);
        return new DramaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DramaViewHolder holder, int position) {
        Drama drama = getItem(position);
        if (drama != null) {
            holder.bindTo(drama);
        }
    }

    public static class DramaViewHolder extends RecyclerView.ViewHolder {

        TextView tvDramaName;
        TextView tvDramaRating;
        MaterialRatingBar materialRatingBar;
        TextView tvUploadDate;
        ImageView ivThumb;


        public DramaViewHolder(View itemView) {
            super(itemView);
            tvDramaName = (TextView) itemView.findViewById(R.id.rcv_drama_tv_name);
            tvDramaRating = (TextView) itemView.findViewById(R.id.rcv_drama_tv_rating);
            tvUploadDate = (TextView) itemView.findViewById(R.id.rcv_drama_tv_updatingdate);
            ivThumb = (ImageView) itemView.findViewById(R.id.rcv_drama_iv_thumb);
            materialRatingBar = (MaterialRatingBar) itemView.findViewById(R.id.rcv_drama_rb_rating);
        }

        public void bindTo(Drama drama) {
            tvDramaName.setText(drama.getName());
            DecimalFormat df=new DecimalFormat("#.#");
            String rating=df.format(drama.getRating());
            tvDramaRating.setText(rating);
            materialRatingBar.setRating(drama.getRating().floatValue());
            Picasso.get().load(drama.getThumb()).fit().placeholder(R.drawable.ic_file_download_green_80dp).into(ivThumb);
            tvUploadDate.setText(drama.getCreatedAt());
        }
    }
}
