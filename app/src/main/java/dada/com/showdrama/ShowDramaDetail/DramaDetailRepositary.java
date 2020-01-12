package dada.com.showdrama.ShowDramaDetail;

import androidx.paging.PagedList;

import dada.com.showdrama.Base.BaseContract;
import dada.com.showdrama.Model.Drama;
import dada.com.showdrama.Model.DramaPack;
import io.reactivex.Observable;

public class DramaDetailRepositary implements BaseContract.IBaseRepositary<Drama> {
    @Override
    public Observable<PagedList<Drama>> getDatalocal() {
        return null;
    }

    @Override
    public Observable<DramaPack> getDataRemote() {
        return null;
    }
}
