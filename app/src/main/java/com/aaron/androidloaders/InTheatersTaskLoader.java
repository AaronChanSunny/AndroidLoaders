package com.aaron.androidloaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.aaron.androidloaders.model.InTheaters;
import com.aaron.androidloaders.network.ApiProvider;
import com.aaron.androidloaders.network.ApiService;

import java.io.IOException;

import retrofit2.Call;

/**
 * Created by Aaron on 2016/2/24.
 */
public class InTheatersTaskLoader extends AsyncTaskLoader<TaskLoaderResult<InTheaters>> {

    private static final String TAG = InTheatersTaskLoader.class.getSimpleName();
    private final Context mContext;
    private TaskLoaderResult<InTheaters> mResult;

    public InTheatersTaskLoader(Context context) {
        super(context);

        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading, mResult " + mResult);
        super.onStartLoading();

        if (mResult != null && mResult.getData() != null) {
            deliverResult(mResult);
        } else {
            forceLoad();
        }
    }

    @Override
    public TaskLoaderResult<InTheaters> loadInBackground() {
        Log.d(TAG, "thread " + Thread.currentThread().getId());

        TaskLoaderResult<InTheaters> result = new TaskLoaderResult<>();

        ApiService api = ApiProvider.getInstance(mContext).getApiService();
        Call<InTheaters> call = api.getInTheaters("福州");
        try {
            result.setData(call.execute().body());
        } catch (IOException e) {
            e.printStackTrace();

            result.setException(e);
        }

        return result;
    }

    @Override
    public void deliverResult(TaskLoaderResult<InTheaters> data) {
        Log.d(TAG, "deliverResult");
        mResult = data;

        super.deliverResult(data);
    }
}
