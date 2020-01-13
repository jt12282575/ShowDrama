package dada.com.showdrama.ShowDramaList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import dada.com.showdrama.Model.Drama;

public class DramaItemCallback {
    public  DiffUtil.ItemCallback<Drama> getDramaItemCallBack(){
        return new DiffUtil.ItemCallback<Drama>() {
            @Override
            public boolean areItemsTheSame(@NonNull Drama oldItem, @NonNull Drama newItem) {
                return oldItem.getDramaId() == newItem.getDramaId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Drama oldItem, @NonNull Drama newItem) {
                return oldItem.equals(newItem);
            }
        };
    }
}
