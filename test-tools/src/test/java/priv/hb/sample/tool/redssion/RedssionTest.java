package priv.hb.sample.tool.redssion;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

import io.vavr.CheckedFunction0;
import io.vavr.concurrent.Future;
import lombok.SneakyThrows;

/**
 * Redssion测试
 *
 * @author hubin
 * @date 2022年10月27日 15:33
 */
public class RedssionTest {
    private static RedissonClient redissonClient;

    @BeforeAll
    public static void init() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://r-bp12fe59fd823b44.redis.rds.aliyuncs.com:6379")
                .setPassword("Yangtuojia001");
        // 创建RedissonClient对象
        redissonClient = Redisson.create(config);


    }

    @Test
    public void test1() throws InterruptedException {
        //获取锁(可重入)，指定锁的名称
        RLock lock = redissonClient.getLock("lockOne");

        //尝试获取锁，参数分别是：获取锁的最大等待时间(期间会重试)，锁自动释放时间，时间单位
        boolean isLock = lock.tryLock(5, 20, TimeUnit.SECONDS);

        //判断获取锁成功
        if (isLock) {
            try {
                System.out.println("执行业务1");
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //释放锁
                lock.unlock();
            }
        }
    }

    @Test
    public void test2() throws InterruptedException {
        //获取锁(可重入)，指定锁的名称
        RLock lock = redissonClient.getLock("lockOne");

        //尝试获取锁，参数分别是：获取锁的最大等待时间(期间会重试)，锁自动释放时间，时间单位
        boolean isLock = lock.tryLock(5, 20, TimeUnit.SECONDS);

        //判断获取锁成功
        if (isLock) {
            try {
                System.out.println("执行业务2");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //释放锁
                lock.unlock();
            }
        }
    }


    /**
     * a1获取到锁,先执行, a2等待a1执行完并获取到锁的情况.
     */
    @SneakyThrows
    @Test
    public void test3() {
        CheckedFunction0<String> a1 = () -> {

            //获取锁(可重入)，指定锁的名称
            RLock lock = redissonClient.getLock("lockOne");

            //尝试获取锁，参数分别是：获取锁的最大等待时间(期间会重试)，锁自动释放时间，时间单位
            boolean isLock = false;
            try {
                isLock = lock.tryLock(5, 20, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //判断获取锁成功
            if (isLock) {
                try {
                    Thread.sleep(3000);
                    System.out.println("执行业务1");
                    return "执行业务1";
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //释放锁
                    lock.unlock();
                }
            }
            return "";
        };

        CheckedFunction0<String> a2 = () -> {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            //获取锁(可重入)，指定锁的名称
            RLock lock = redissonClient.getLock("lockOne");

            //尝试获取锁，参数分别是：获取锁的最大等待时间(期间会重试)，锁自动释放时间，时间单位
            boolean isLock = false;
            try {
                isLock = lock.tryLock(5, 20, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //判断获取锁成功
            if (isLock) {
                try {
                    Thread.sleep(6000);
                    System.out.println("执行业务2");
                    return "执行业务2";
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //释放锁
                    lock.unlock();
                }
            }
            return "";
        };


        Future<String> of1 = Future.of(a1);
        Future<String> of2 = Future.of(a2);


        Thread.sleep(100000);
    }


    /**
     * 测试a2先运行,a1等待锁超时的情况
     */
    @SneakyThrows
    @Test
    public void test4() {
        CheckedFunction0<String> a1 = () -> {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            //获取锁(可重入)，指定锁的名称
            RLock lock = redissonClient.getLock("lockOne");

            //尝试获取锁，参数分别是：获取锁的最大等待时间(期间会重试)，锁自动释放时间，时间单位
            boolean isLock = false;
            try {
                isLock = lock.tryLock(5, 20, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //判断获取锁成功
            if (isLock) {
                try {
                    Thread.sleep(3000);
                    System.out.println("执行业务1");
                    return "执行业务1";
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //释放锁
                    lock.unlock();
                }
            }
            return "";
        };

        CheckedFunction0<String> a2 = () -> {
            //获取锁(可重入)，指定锁的名称
            RLock lock = redissonClient.getLock("lockOne");

            //尝试获取锁，参数分别是：获取锁的最大等待时间(期间会重试)，锁自动释放时间，时间单位
            boolean isLock = false;
            try {
                isLock = lock.tryLock(5, 20, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //判断获取锁成功
            if (isLock) {
                try {
                    Thread.sleep(6000);
                    System.out.println("执行业务2");
                    return "执行业务2";
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //释放锁
                    lock.unlock();
                }
            }
            return "";
        };


        Future<String> of1 = Future.of(a1);
        Future<String> of2 = Future.of(a2);


        Thread.sleep(100000);
    }


}
