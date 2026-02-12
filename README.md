# ignite-reproducer

Проект с демонстрацией ошибки десериализации в Ignite при использовании динамически загруженных классов через PeerClassloading или DeploymentSpi внутри коллекций.

## Структура проекта

```
ignite-reproducer/
├── client/          # Клиентское приложение
├── server/          # Серверное приложение
├── gradle/          # Конфигурация Gradle Wrapper
├── build.gradle     # Корневой build-файл
└── settings.gradle  # Настройки проекта
```

## Требования

- Java Development Kit (JDK) версии 17
- Gradle (используется Gradle Wrapper, доступный в репозитории)

## Запуск проекта

Для запуска проекта необходимо выполнить команды в следующем порядке:

### 1. Запуск сервера

```bash
./gradlew server:run
```

Эта команда собирает и запускает серверное приложение. Сервер будет доступен на настроенном порту (подробности в документации модуля server).

### 2. Запуск клиента

После успешного запуска сервера откройте новый терминал и выполните:

```bash
./gradlew client:run
```

Клиент подключится к запущенному серверу и начнёт взаимодействие с ним.

В логах серверного узла должны быть видны подобные сообщения:

```=== Starting cache operations on server side ===

--- Test 1: Caching single MyPojo object ---
Putting: MyPojo{value='test value', timestamp=1770901407477}
Successfully cached single MyPojo
Retrieved: MyPojo{value='test value', timestamp=1770901407477}
Class: com.reproducer.MyPojo
Test 1: SUCCESS

--- Test 2: Caching ArrayList with MyPojo ---
Putting: [42, string value, MyPojo{value='test value', timestamp=1770901407477}]
Successfully cached ArrayList
ERROR in cache operation: class org.apache.ignite.IgniteCheckedException: com.reproducer.MyPojo
javax.cache.CacheException: class org.apache.ignite.IgniteCheckedException: com.reproducer.MyPojo
at org.apache.ignite.internal.processors.cache.GridCacheUtils.convertToCacheException(GridCacheUtils.java:1243)
at org.apache.ignite.internal.processors.cache.IgniteCacheProxyImpl.cacheException(IgniteCacheProxyImpl.java:2078)
at org.apache.ignite.internal.processors.cache.IgniteCacheProxyImpl.get(IgniteCacheProxyImpl.java:1110)
at org.apache.ignite.internal.processors.cache.GatewayProtectedCacheProxy.get(GatewayProtectedCacheProxy.java:656)
at com.reproducer.ClientApp$GetCacheTask.run(ClientApp.java:71)
at org.apache.ignite.internal.processors.closure.GridClosureProcessor$C4.execute(GridClosureProcessor.java:1785)
at org.apache.ignite.internal.processors.job.GridJobWorker$1.call(GridJobWorker.java:628)
at org.apache.ignite.internal.util.IgniteUtils.wrapThreadLoader(IgniteUtils.java:7498)
at org.apache.ignite.internal.processors.job.GridJobWorker.execute0(GridJobWorker.java:622)
at org.apache.ignite.internal.processors.job.GridJobWorker.body(GridJobWorker.java:547)
at org.apache.ignite.internal.util.worker.GridWorker.run(GridWorker.java:125)
at org.apache.ignite.internal.processors.job.GridJobProcessor.processJobExecuteRequest(GridJobProcessor.java:1441)
at org.apache.ignite.internal.processors.job.GridJobProcessor$JobExecutionListener.onMessage(GridJobProcessor.java:2223)
at org.apache.ignite.internal.managers.communication.GridIoManager.invokeListener(GridIoManager.java:1906)
at org.apache.ignite.internal.managers.communication.GridIoManager.processRegularMessage0(GridIoManager.java:1527)
at org.apache.ignite.internal.managers.communication.GridIoManager.access$5300(GridIoManager.java:242)
at org.apache.ignite.internal.managers.communication.GridIoManager$9.execute(GridIoManager.java:1420)
at org.apache.ignite.internal.managers.communication.TraceRunnable.run(TraceRunnable.java:55)
at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
at java.base/java.lang.Thread.run(Thread.java:833)
Caused by: class org.apache.ignite.IgniteCheckedException: com.reproducer.MyPojo
at org.apache.ignite.internal.util.IgniteUtils.cast(IgniteUtils.java:8007)
at org.apache.ignite.internal.util.future.GridFutureAdapter.resolve(GridFutureAdapter.java:263)
at org.apache.ignite.internal.util.future.GridFutureAdapter.get0(GridFutureAdapter.java:175)
at org.apache.ignite.internal.util.future.GridFutureAdapter.get(GridFutureAdapter.java:144)
at org.apache.ignite.internal.processors.cache.GridCacheAdapter.get(GridCacheAdapter.java:4688)
at org.apache.ignite.internal.processors.cache.GridCacheAdapter.repairableGet(GridCacheAdapter.java:4648)
Caused by: class org.apache.ignite.IgniteCheckedException: com.reproducer.MyPojo

	at org.apache.ignite.internal.processors.cache.GridCacheAdapter.get(GridCacheAdapter.java:1331)
	at org.apache.ignite.internal.processors.cache.IgniteCacheProxyImpl.get(IgniteCacheProxyImpl.java:1107)
	... 18 more
Caused by: class org.apache.ignite.binary.BinaryInvalidTypeException: com.reproducer.MyPojo
at org.apache.ignite.internal.binary.BinaryContext.descriptorForTypeId(BinaryContext.java:741)
at org.apache.ignite.internal.binary.BinaryReaderExImpl.deserialize0(BinaryReaderExImpl.java:1772)
at org.apache.ignite.internal.binary.BinaryReaderExImpl.deserialize(BinaryReaderExImpl.java:1731)
at org.apache.ignite.internal.binary.BinaryObjectImpl.deserializeValue(BinaryObjectImpl.java:866)
Caused by: class org.apache.ignite.binary.BinaryInvalidTypeException: com.reproducer.MyPojo

	at org.apache.ignite.internal.binary.BinaryObjectImpl.value(BinaryObjectImpl.java:198)
	at org.apache.ignite.internal.processors.cache.CacheObjectUtils.unwrapBinary(CacheObjectUtils.java:199)
	at org.apache.ignite.internal.processors.cache.CacheObjectUtils.unwrapKnownCollection(CacheObjectUtils.java:104)
	at org.apache.ignite.internal.processors.cache.CacheObjectUtils.unwrapBinary(CacheObjectUtils.java:203)
	at org.apache.ignite.internal.processors.cache.CacheObjectUtils.unwrapBinaryIfNeeded(CacheObjectUtils.java:78)
	at org.apache.ignite.internal.processors.cache.CacheObjectContext.unwrapBinaryIfNeeded(CacheObjectContext.java:138)
	at org.apache.ignite.internal.processors.cache.GridCacheContext.unwrapBinaryIfNeeded(GridCacheContext.java:1767)
	at org.apache.ignite.internal.processors.cache.distributed.dht.GridPartitionedSingleGetFuture.setResult(GridPartitionedSingleGetFuture.java:765)
	at org.apache.ignite.internal.processors.cache.distributed.dht.GridPartitionedSingleGetFuture.localGet(GridPartitionedSingleGetFuture.java:549)
	at org.apache.ignite.internal.processors.cache.distributed.dht.GridPartitionedSingleGetFuture.tryLocalGet(GridPartitionedSingleGetFuture.java:434)
	at org.apache.ignite.internal.processors.cache.distributed.dht.GridPartitionedSingleGetFuture.mapKeyToNode(GridPartitionedSingleGetFuture.java:401)
	at org.apache.ignite.internal.processors.cache.distributed.dht.GridPartitionedSingleGetFuture.map(GridPartitionedSingleGetFuture.java:278)
	at org.apache.ignite.internal.processors.cache.distributed.dht.GridPartitionedSingleGetFuture.init(GridPartitionedSingleGetFuture.java:242)
	at org.apache.ignite.internal.processors.cache.distributed.dht.atomic.GridDhtAtomicCache.getAsync0(GridDhtAtomicCache.java:1423)
	at org.apache.ignite.internal.processors.cache.distributed.dht.atomic.GridDhtAtomicCache.access$1200(GridDhtAtomicCache.java:147)
	at org.apache.ignite.internal.processors.cache.distributed.dht.atomic.GridDhtAtomicCache$12.apply(GridDhtAtomicCache.java:467)
	at org.apache.ignite.internal.processors.cache.distributed.dht.atomic.GridDhtAtomicCache$12.apply(GridDhtAtomicCache.java:465)
	at org.apache.ignite.internal.processors.cache.distributed.dht.atomic.GridDhtAtomicCache.asyncOp(GridDhtAtomicCache.java:752)
	at org.apache.ignite.internal.processors.cache.distributed.dht.atomic.GridDhtAtomicCache.getAsync(GridDhtAtomicCache.java:465)
	at org.apache.ignite.internal.processors.cache.GridCacheAdapter.get(GridCacheAdapter.java:4682)
	... 21 more
Caused by: java.lang.ClassNotFoundException: com.reproducer.MyPojo
at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:641)
Caused by: java.lang.ClassNotFoundException: com.reproducer.MyPojo

	at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
	at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:520)
	at java.base/java.lang.Class.forName0(Native Method)
	at java.base/java.lang.Class.forName(Class.java:467)
	at org.apache.ignite.internal.util.IgniteUtils.forName(IgniteUtils.java:9373)
	at org.apache.ignite.internal.util.IgniteUtils.forName(IgniteUtils.java:9311)
	at org.apache.ignite.internal.MarshallerContextImpl.getClass(MarshallerContextImpl.java:384)
	at org.apache.ignite.internal.binary.BinaryContext.descriptorForTypeId(BinaryContext.java:717)
	... 44 more
```