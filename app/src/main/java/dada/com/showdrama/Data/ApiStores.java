package dada.com.showdrama.Data;

import dada.com.showdrama.Model.DramaPack;
import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ApiStores {
    String BASE_URL = "http://www.mocky.io/";

    @GET("v2/5a97c59c30000047005c1ed2")
    Observable<DramaPack> getDramaPack();

}
