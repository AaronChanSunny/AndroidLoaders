# AndroidLoaders

## 前面的话

App大部分的业务场景是这样：从服务端拉取数据，返回结果后进行展示。Android中，每一个界面都是由Activity或者Fragment托管的，通常情况下，业务的逻辑和生命周期强耦合。很多时候，我们需要决定什么时候拉取数据，什么时候使用缓存数据。例如：

- 首次进入页面
- 设备[配置发生变化](http://developer.android.com/guide/topics/resources/runtime-changes.html?utm_campaign=adp_series_loaders_020216&utm_source=medium&utm_medium=blog)

以上两种情况，拉取数据的策略是不同的。我们希望在首次进入页面的时候进行数据拉取，但是当配置发生变化时，直接使用上次请求得到的数据。那么，有没有一种较为优雅的解决方案？有的，那就是[Loader](http://developer.android.com/guide/components/loaders.html)。
