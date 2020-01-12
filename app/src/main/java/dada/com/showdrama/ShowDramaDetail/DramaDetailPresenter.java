package dada.com.showdrama.ShowDramaDetail;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.paging.PagedList;

import dada.com.showdrama.Base.BasePresenter;
import dada.com.showdrama.Model.Drama;
import dada.com.showdrama.ShowDramaList.DramaListRepository;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;


public class DramaDetailPresenter extends BasePresenter<IDramaDetailView> {
    private DramaListRepository dramaListRepository;
    private static final String TAG = "DramaDetailPresenter";

    public DramaDetailPresenter(IDramaDetailView iView) {
        attachView(iView);
    }
    public void getDrama(Intent intent) {
        Drama drama = (Drama) intent.getSerializableExtra(DramaDetailActivity.DETAIL_DRAMA);
        if(drama!= null ) mvpView.setDrama(drama);
        else{
            // get id from url and get drama from repositary
            String action = intent.getAction();
            String data = intent.getDataString();
            if (Intent.ACTION_VIEW.equals(action) && data != null) {
                Log.i(TAG, "getDrama data: "+data);
                String dramaidstr = data.substring(data.lastIndexOf("/") + 1);
                try {
                    if (dramaListRepository == null) dramaListRepository = new DramaListRepository();
                    int dramaid = Integer.parseInt(dramaidstr);
                    Log.i(TAG, "getDrama: id: "+dramaid);
                    mvpView.showLoading();
                    DisposableSingleObserver dataObserver = new DisposableSingleObserver<Drama>() {
                        @Override
                        public void onSuccess(Drama drama) {
                            mvpView.setDrama(drama);
                            mvpView.hideLoading();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mvpView.hideLoading();
                            Log.i(TAG, "onError: 找不到該劇 "+e.getMessage());
                            mvpView.noDrama(e.getMessage());
                        }
                    };
                    addSubScribe(dramaListRepository.getDramaFromDb(dramaid),dataObserver);
                    // search drama
                } catch (NumberFormatException e) {
                    Log.i(TAG, "getDrama: 並不是數字");
                    e.printStackTrace();
                    mvpView.noDrama(e.getMessage());
                }

            }

            if (dramaListRepository == null) dramaListRepository = new DramaListRepository();
            int dramaid = 0;

        }


    }


}
