## loader-facade-sdk使用文档

## step1

加载依赖

```xml

<dependency>
    <groupId>com.metevs.dcache</groupId>
    <artifactId>dcache-facade-sdk</artifactId>
    <version>v1.0.0</version>
</dependency>

```

## step2 

配置参数，不需要额外编写dubbo配置文件

通过工厂获取具体实现类

```java
FacadeParams facadeParams = new FacadeParams();
        facadeParams.setApplicationName("your-application-name")
                .setFacadeVersion("1.0.0")
                .setProtocol("zookeeper")
                .setZkHost("192.168.110.11:2181")
                .setTimeout(10000);
CacheLoaderFacade cacheLoaderFacade = SimpleFacadeCacheLoaderFactory.getInstance(facadeParams);        

```

## step3 

视业务场景获取缓存数据

已支持如下类型：

* 支持loader配置的的业务逻辑；

调用示例：

```java
List<XXXX> drivingRecordCaches = cacheLoaderFacade.queryDrivingRecordCache(DrTeamQueryParam.newDrivingRecordBuilder()
                .setCityCode("330100")
                .sortFind(SortOrder.ASC, DrivingRecordField.DISPATCH_ARRIVE_TIME)
                .build());

```

其他业务同理