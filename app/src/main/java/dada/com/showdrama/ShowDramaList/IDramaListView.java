package dada.com.showdrama.ShowDramaList;

import androidx.paging.PagedList;

import java.util.List;
import java.util.Observable;

import dada.com.showdrama.Base.BaseContract;
import dada.com.showdrama.Model.Drama;

public interface IDramaListView extends BaseContract.IBaseView {
    void getDramasSuccess(PagedList<Drama> dramas);
    void getDramaFail(String msg);
    String getSearchKeyword();
    boolean checkSearchKeywordNotNull(String searchKeyword);
}
