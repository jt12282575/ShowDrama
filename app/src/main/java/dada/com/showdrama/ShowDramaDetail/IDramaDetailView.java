package dada.com.showdrama.ShowDramaDetail;

import dada.com.showdrama.Base.BaseContract;
import dada.com.showdrama.Model.Drama;

public interface IDramaDetailView extends BaseContract.IBaseView {
    void setDrama(Drama drama);
    void noDrama(String msg);
}
