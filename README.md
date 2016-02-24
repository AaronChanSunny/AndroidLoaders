# AndroidLoaders

## 前面的话

App大部分的业务场景是这样：从服务端拉取数据，返回结果后进行展示。Android中，每一个界面都是由Activity或者Fragment托管的，通常情况下，业务的逻辑和生命周期强耦合。很多时候，我们需要决定什么时候拉取数据，什么时候使用缓存数据。例如：

- 首次进入页面
- 设备[配置发生变化](http://developer.android.com/guide/topics/resources/runtime-changes.html?utm_campaign=adp_series_loaders_020216&utm_source=medium&utm_medium=blog)

以上两种情况，拉取数据的策略是不同的。我们希望在首次进入页面的时候进行数据拉取，但是当配置发生变化时，直接使用上次请求得到的数据。那么，有没有一种较为优雅的解决方案？有的，那就是[Loaders](http://developer.android.com/guide/components/loaders.html)。

## 认识Loaders
使用Loaders可以大大简化Activity或者Fragment加载异步数据。引用官方一句话：

> Loaders make it easy to asynchronously load data in an activity or fragment. 

谈到异步任务，最基础的莫过于Handler了。但是Handler在实际过程中较为复杂，编写的代码可读性也较差。Android为了降低异步编程的复杂度，引入了[AsyncTask](http://developer.android.com/reference/android/os/AsyncTask.html)。Loaders的设计目的也是为了简化数据的异步操作，并且，在使用上和AsyncTask十分相似。它具有如下特点：

- They are available to every Activity and Fragment.（就地取材）
- They provide asynchronous loading of data.（用途）
- They monitor the source of their data and deliver new results when the content changes.（感知数据变化）
- They automatically reconnect to the last loader's cursor when being recreated after a configuration change. Thus, they don't need to re-query their data.

Loaders的强大之处在于后面2点。如果对每个数据源编写对应`Observer`，当数据源发生变化时，可以通知Loaders重新加载数据；手机发生转屏导致Activity或者Fragment重建，Loaders可以直接使用上次请求的结果，而不需要再次请求。

## 使用它

## 总结
