package com.aaron.androidloaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.aaron.androidloaders.model.InTheaters;

/**
 * Created by Aaron on 2016/2/24.
 */
public class InTheatersTaskLoader extends AsyncTaskLoader<InTheaters> {

    private static final String TAG = InTheatersTaskLoader.class.getSimpleName();
    private final Context mContext;
    private InTheaters mData;

    public InTheatersTaskLoader(Context context) {
        super(context);

        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading, mData " + mData);
        super.onStartLoading();

        if (mData != null) {
            deliverResult(mData);
        } else {
            forceLoad();
        }
    }

    @Override
    public InTheaters loadInBackground() {
        Log.d(TAG, "thread " + Thread.currentThread().getId());

//        ApiService api = ApiProvider.getInstance(mContext).getApiService();
//        Call<InTheaters> call = api.getInTheaters("福州");
//        try {
//            return call.execute().body();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return null;
    }

    @Override
    public void deliverResult(InTheaters data) {
        Log.d(TAG, "deliverResult");
        mData = data;

        super.deliverResult(data);
    }
}
