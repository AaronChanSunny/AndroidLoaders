# Android TaskLoader与LoaderManager，让你更优雅地异步加载数据

## 1 前面的话

App大部分的业务场景是这样：从服务端拉取数据，返回结果后进行展示。Android中，每一个界面都是由Activity或者Fragment托管的，通常情况下，业务的逻辑和生命周期强耦合。很多时候，我们需要决定什么时候拉取数据，什么时候使用缓存数据。例如：

- 首次进入页面
- 设备[配置发生变化](http://developer.android.com/guide/topics/resources/runtime-changes.html?utm_campaign=adp_series_loaders_020216&utm_source=medium&utm_medium=blog)

以上两种情况，拉取数据的策略是不同的。我们希望在首次进入页面的时候进行数据拉取，但是当配置发生变化时，直接使用上次请求得到的数据。那么，有没有一种较为优雅的解决方案？有的，那就是[Loaders](http://developer.android.com/guide/components/loaders.html)。

## 2 认识Loaders
使用Loaders可以大大简化Activity或者Fragment加载异步数据。引用官方一句话：

> Loaders make it easy to asynchronously load data in an activity or fragment. 

谈到异步任务，最基础的莫过于Handler了。但是Handler在实际过程中较为复杂，编写的代码可读性也较差。Android为了降低异步编程的复杂度，引入了[AsyncTask](http://developer.android.com/reference/android/os/AsyncTask.html)。Loaders的设计目的也是为了简化数据的异步操作，并且，在使用上和AsyncTask十分相似。它具有如下特点：

- They are available to every Activity and Fragment.（就地取材）
- They provide asynchronous loading of data.（用途）
- They monitor the source of their data and deliver new results when the content changes.（感知数据变化）
- They automatically reconnect to the last loader's cursor when being recreated after a configuration change. Thus, they don't need to re-query their data.

Loaders的强大之处在于后面2点。如果对每个数据源编写对应`Observer`，当数据源发生变化时，可以通知Loaders重新加载数据；手机发生转屏导致Activity或者Fragment重建，Loaders可以直接使用上次请求的结果，而不需要再次请求。

## 3 使用它

使用Loaders做数据异步加载，包括两部分：指定TaskLoader，使用LoaderManager管理TaskLoader。

### 3.1 定义TaskLoader
官方[例子](http://developer.android.com/guide/components/loaders.html#example)是读取手机通讯录，这里我们使用一种更通用的场景，网络请求。当然，TaskLoader可以是任何你需要异步的操作集合。Api使用[豆瓣电影](https://developers.douban.com/wiki/?title=movie_v2)，定义一个`InTheatersTaskLoader`，用于请求`正在热映`，相关代码如下：

**TaskLoaderResult**

>保存异步任务结果，包括数据和异常信息。

```
public class TaskLoaderResult<T> {

    private Exception mException;
    private T mData;

    public TaskLoaderResult() {
    }

    public TaskLoaderResult(Exception exception, T data) {
        mException = exception;
        mData = data;
    }

    public Exception getException() {
        return mException;
    }

    public void setException(Exception exception) {
        mException = exception;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }
}
```

**InTheaters**

>Json对象，用于Gson序列化/反序列化。这里使用推荐Android Studio神级插件[GsonFormat](https://github.com/zzz40500/GsonFormat)进行自动生成。

```
public class TaskLoaderResult<T> {

    private Exception mException;
    private T mData;

    public TaskLoaderResult() {
    }

    public TaskLoaderResult(Exception exception, T data) {
        mException = exception;
        mData = data;
    }

    public Exception getException() {
        return mException;
    }

    public void setException(Exception exception) {
        mException = exception;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }
}
```

**AsyncTaskLoader**

>所有的异步操作都应该放在这里，并负责保存结果。

```
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
```
这里，我们就定义好了一个异步加载任务，用来从豆瓣拉取最新`正在热映`的电影。关于Api部分的封装，使用的是Retrofit + OkHttp组合，这里就不列出代码了。

### 3.2 使用LoaderManager管理Loader

定义好LoaderTask之后，我们还需要让外部启动它，才能完成数据加载。Android在Activity/Fragment中已经封装好了一个LoaderManager，直接调用`getLoaderManager()`，如果需要支持v4包，则调用兼容方法`getSupportLoaderManager()`。此外，需要让Activity/Fragment实现`LoaderManager.LoaderCallbacks`。代码如下：

```
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<TaskLoaderResult<InTheaters>>, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private List<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mData = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R
                .id.text1, mData);

        mListView = (ListView) findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(findViewById(R.id.empty_item));

        getSupportLoaderManager().initLoader(0, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                getSupportLoaderManager().restartLoader(0, null, MainActivity.this);
            }
        });

        findViewById(R.id.button).setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<TaskLoaderResult<InTheaters>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader, id " + id);

        return new InTheatersTaskLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<TaskLoaderResult<InTheaters>> loader, TaskLoaderResult<InTheaters> data) {
        if (data.getException() != null) {
            Log.e(TAG, data.getException().getMessage());
            return;
        }

        for(InTheaters.Subject subject : data.getData().getSubjects()) {
            mData.add(subject.getTitle());
        }

        mAdapter.addAll(mData);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<TaskLoaderResult<InTheaters>> loader) {
        Log.d(TAG, "onLoaderReset");

        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            startActivity(new Intent(this, FooActivity.class));
        }
    }
}
```

## 4 总结

- 多个LoaderTask 

如果一个Activity有多个LoaderTask，由于LoaderTask的id和处理的数据类型都不同，则需要为每个LoaderTask定义一个回调。
- 异常处理

网络请求都包含异常处理逻辑，这里采用的是单独封装一个TaskResult类。如果抛出以异常，将异常信息set进去，并且data至为null。这时候Activity/Fragment在`onFinished()`中需要对TaskResult进行判断，如果异常信息不为空，进入异常处理逻辑。

## 参考

- [Android Developers API Guideds](http://developer.android.com/guide/components/loaders.html)
- [Making loading data lifecycle aware](https://medium.com/google-developers/making-loading-data-on-android-lifecycle-aware-897e12760832#.rkjlbux9a)
- [Android: AsyncTaskLoader Exception Handling](http://blog.gunawan.me/2011/10/android-asynctaskloader-exception.html?m=1)
