package priv.hb.sample.tool.guava.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.vavr.collection.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hubin
 * @date 2022年10月13日 17:50
 */
@Slf4j
public class Test1 {

    @Test
    public void test1() {
        //实例化缓存构建器
        CacheBuilder cacheBuilder = CacheBuilder.newBuilder();
        // 构建缓存容器
        Cache<String, String> cache = cacheBuilder.build();// 缓存数据
        cache.put("cache", "cache-value");
        // 获取缓存数据
        String value = cache.getIfPresent("cache");

        System.out.println(value);
        // 删除缓存
        cache.invalidate("cache");
        String value2 = cache.getIfPresent("cache");
        System.out.println(value2);
    }


    /**
     * 带过期时间和容量的缓存
     */
    @Test
    @SneakyThrows
    public void test2() {
        Cache<String, String> cache = CacheBuilder.newBuilder()
                // Guava 默认会根据LRU策略回收缓存项来保证不超过最大数目
                .maximumSize(2)
                // 缓存项（Key）在给定时间范围内没有写访问，那么下次访问时，会回收该Key
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .build();
        cache.put("key1", "value1");

        List.range(0, 10).forEach(time -> {
            System.out.println("第" + time + "次取到key1的值为：" + cache.getIfPresent("key1"));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * 带过期时间和容量的缓存
     */
    @Test
    @SneakyThrows
    public void test3() {
        Cache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(2)
                // 缓存项（Key）在给定时间范围内没有读/写访问，那么下次访问时，会回收该Key
                .expireAfterAccess(2, TimeUnit.SECONDS)
                .build();
        cache.put("key1", "value1");

        List.range(0, 10).forEach(time -> {
            System.out.println("第" + time + "次取到key1的值为：" + cache.getIfPresent("key1"));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }


    @Test
    public void testListener() {
        RemovalListener<String, String> removalListener = new RemovalListener<String,String>() {
            @Override
            public void onRemoval(RemovalNotification<String, String> notification) {
                String key = notification.getKey();
                //例如是数据库连接，这里可以close该连接
                String value = notification.getValue();

                log.warn("key:{}, value:{} 过期删除通知", key, value);
            }
        };

        Cache<String, String> cache = CacheBuilder.newBuilder()
                .expireAfterWrite(2, TimeUnit.SECONDS)
                .removalListener(removalListener)
                .build();

        cache.put("key1", "value1");

        List.range(0, 10).forEach(time -> {
            System.out.println("第" + time + "次取到key1的值为：" + cache.getIfPresent("key1"));
            // 手动让key过期, 不然可能时间到期后,key查不到,但是实际还未删除,就不会触发监听器.
            cache.invalidate("key1");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


    }


    @Test
    public void test() throws ExecutionException {

        Cache<String, String> cache = CacheBuilder.newBuilder().maximumSize(1000).build();
        String resultVal = cache.get("code", new Callable<String>() {
            public String call() {
                String strProValue = "begin " + "code" + "!";
                return strProValue;
            }
        });
        System.out.println("value : " + resultVal); //value : begin code!


    }


}
