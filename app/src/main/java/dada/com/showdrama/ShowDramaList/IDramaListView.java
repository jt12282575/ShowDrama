package dada.com.showdrama.ShowDramaList;

import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedList;

import java.util.List;

import dada.com.showdrama.Base.BaseContract;
import dada.com.showdrama.Model.Drama;

public interface IDramaListView extends BaseContract.IBaseView {
    void getDramasSuccess(PagedList<Drama> dramas);
    void getDramaFail(String msg);
    String getCurrentQuery();
    String getLastTimeQuery();
    void getLastTimeHistory();
    void saveEndStateAndHistory();
    void setLastQuery(String lastQuery);
    void hideKeyboard();
    void finishRefresh();
    void addHistoryWords(); // 裡面也要做 swapping
    void updateDramaList(List<Drama> dramaList);


}
