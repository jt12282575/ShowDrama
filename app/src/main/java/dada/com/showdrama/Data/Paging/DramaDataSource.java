package dada.com.showdrama.Data.Paging;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import java.util.List;

import dada.com.showdrama.Model.Drama;
import dada.com.showdrama.Util.Constant;

public class DramaDataSource extends PageKeyedDataSource<Integer, Drama> {

    public static final int PAGE_SIZE = Constant.PAGESIZE;
    private DramaListProvider provider;

    public DramaDataSource(DramaListProvider provider) {
        this.provider = provider;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Drama> callback) {
        List<Drama> result = provider.getDramaList(0, params.requestedLoadSize);
        callback.onResult(result, 1, 2);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Drama> callback) {
        List<Drama> result = provider.getDramaList(params.key, params.requestedLoadSize);
        Integer nextIndex = null;

        if (params.key > 1) {
            nextIndex = params.key - 1;
        }
        callback.onResult(result, nextIndex);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Drama> callback) {
        List<Drama> result = provider.getDramaList(params.key, params.requestedLoadSize);
        callback.onResult(result, params.key + 1);
    }
}