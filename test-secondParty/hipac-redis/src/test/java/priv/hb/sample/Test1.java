package priv.hb.sample;

import com.yangt.asd.redis.cache.Cache;
import com.yangt.asd.redis.cache.CacheBuilder;
import com.yangt.asd.redis.cache.CacheConfig;
import com.yangt.asd.redis.cache.serializer.JsonRedisSerializer;
import com.yangt.asd.redis.cache.serializer.StringRedisSerializer;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Set;


public class Test1 {

    @Test
    public void testString() {
        CacheBuilder cacheBuilder = null;
        try {
            cacheBuilder = new CacheBuilder().setCacheConfig(
                            new CacheConfig().setHost("r-bp12fe59fd823b44.redis.rds.aliyuncs.com")
                                    .setPort(6379)
                                    .setPassword("Yangtuojia001")
                                    .setDbIndex(0))
                    .setKeySerializer(new StringRedisSerializer())
                    .setValueSerializer(new JsonRedisSerializer());
            Cache cache = cacheBuilder.build();
            cache.set("hbaaaa", "123");
            String res = cache.get("hbaaaa");
            System.out.println(res);
            Assert.assertTrue("123".equals(res));
        } finally {
            cacheBuilder.destroy();
        }
    }

    @Test
    public void testStringByTTL() {
        CacheBuilder cacheBuilder = null;
        try {
            cacheBuilder = new CacheBuilder().setCacheConfig(
                            new CacheConfig()
                                    .setHost("r-bp12fe59fd823b44.redis.rds.aliyuncs.com")
                                    .setPort(6379)
                                    .setPassword("Yangtuojia001")
                                    .setDbIndex(0))
                    .setKeySerializer(new StringRedisSerializer())
                    .setValueSerializer(new JsonRedisSerializer());
            Cache cache = cacheBuilder.build();
            cache.set("hbaaaa2", "100TTL", 100);
            String res = cache.get("hbaaaa2");
            System.out.println(res);
            Assert.assertTrue("100TTL".equals(res));
        } finally {
            cacheBuilder.destroy();
        }
    }


    /**
     *
     */
    @Test
    public void testSet() {
        CacheBuilder cacheBuilder = null;
        try {
            cacheBuilder = new CacheBuilder().setCacheConfig(
                            new CacheConfig()
                                    .setHost("r-bp12fe59fd823b44.redis.rds.aliyuncs.com")
                                    .setPort(6379)
                                    .setPassword("Yangtuojia001")
                                    .setDbIndex(0))
                    .setKeySerializer(new StringRedisSerializer())
                    .setValueSerializer(new JsonRedisSerializer());
            Cache cache = cacheBuilder.build();
            cache.sadd("hbSetKey", "Setvalue1", "Setvalue2");
            cache.sadd("hbSetKey", "Setvalue3");

            Set<Object> res = cache.smembers("hbSetKey");
            System.out.println(res);
        } finally {
            cacheBuilder.destroy();
        }
    }


    /**
     * 测试给单个key增加过期时间
     */
    @Test
    public void testExpire() {
        CacheBuilder cacheBuilder = null;
        try {
            cacheBuilder = new CacheBuilder().setCacheConfig(
                            new CacheConfig()
                                    .setHost("r-bp12fe59fd823b44.redis.rds.aliyuncs.com")
                                    .setPort(6379)
                                    .setPassword("Yangtuojia001")
                                    .setDbIndex(0))
                    .setKeySerializer(new StringRedisSerializer())
                    .setValueSerializer(new JsonRedisSerializer());
            Cache cache = cacheBuilder.build();
            cache.expire("hbSetKey", 100);
        } finally {
            cacheBuilder.destroy();
        }
    }


    /**
     *
     */
    @Test
    public void testHash() {
        CacheBuilder cacheBuilder = null;
        try {
            cacheBuilder = new CacheBuilder().setCacheConfig(
                            new CacheConfig()
                                    .setHost("r-bp12fe59fd823b44.redis.rds.aliyuncs.com")
                                    .setPort(6379)
                                    .setPassword("Yangtuojia001")
                                    .setDbIndex(0))
                    .setKeySerializer(new StringRedisSerializer())
                    .setValueSerializer(new JsonRedisSerializer());
            Cache cache = cacheBuilder.build();
            cache.hset("hbHashKey", "key1", "100");
            cache.hset("hbHashKey", "key2", "200");
            cache.hset("hbHashKey", "key3", "300");
            cache.hset("hbHashKey", "key4", 400);
            cache.expire("hbHashKey", 100);


            Map<Object, Object> hbHashKey = cache.hgetall("hbHashKey");
            System.out.println(hbHashKey);
        } finally {
            cacheBuilder.destroy();
        }
    }


    @Test
    public void testHashIncrease() {
        CacheBuilder cacheBuilder = null;
        try {
            cacheBuilder = new CacheBuilder().setCacheConfig(
                            new CacheConfig()
                                    .setHost("r-bp12fe59fd823b44.redis.rds.aliyuncs.com")
                                    .setPort(6379)
                                    .setPassword("Yangtuojia001")
                                    .setDbIndex(0))
                    .setKeySerializer(new StringRedisSerializer())
                    .setValueSerializer(new JsonRedisSerializer());
            Cache cache = cacheBuilder.build();
            cache.hincr("hbHashKey", "key4", 1);
            Map<Object, Object> hbHashKey = cache.hgetall("hbHashKey");
            System.out.println(hbHashKey);
        } finally {
            cacheBuilder.destroy();
        }
    }

    @Test
    public void testZset() {
        CacheBuilder cacheBuilder = null;
        try {
            cacheBuilder = new CacheBuilder().setCacheConfig(
                            new CacheConfig()
                                    .setHost("r-bp12fe59fd823b44.redis.rds.aliyuncs.com")
                                    .setPort(6379)
                                    .setPassword("Yangtuojia001")
                                    .setDbIndex(0))
                    .setKeySerializer(new StringRedisSerializer())
                    .setValueSerializer(new JsonRedisSerializer());
            Cache cache = cacheBuilder.build();
            cache.zadd("hbZsetKey", 100.0, "value1");
            cache.zadd("hbZsetKey", 200.0, "value2");
            cache.zadd("hbZsetKey", 10.0, "value3");

            cache.expire("hbZsetKey", 100);


            Set<Object> res = cache.zrange("hbZsetKey", 0 , 100);
            System.out.println(res);
        } finally {
            cacheBuilder.destroy();
        }
    }
}
