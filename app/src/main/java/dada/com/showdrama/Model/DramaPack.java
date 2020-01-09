

package dada.com.showdrama.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;



public class DramaPack {

    @SerializedName("data")
    @Expose
    private List<Drama> data = null;

    public List<Drama> getData() {
        return data;
    }

    public void setData(List<Drama> data) {
        this.data = data;
    }



}
