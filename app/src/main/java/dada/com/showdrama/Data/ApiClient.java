package dada.com.showdrama.Data;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
    public static Retrofit retrofit;

    // Singleton
    public static Retrofit retrofit(){
        if(retrofit == null){
            OkHttpClient.Builder builder = new OkHttpClient.Builder(); // 建立 OkHttpClient，有附加訊息加在這裡

            OkHttpClient okHttpClient = builder.build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiStores.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();

        }

        return retrofit;
    }


}
