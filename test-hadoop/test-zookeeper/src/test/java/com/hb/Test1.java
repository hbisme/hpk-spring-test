package com.hb;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.client.ConnectStringParser;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Test1 {

    ExponentialBackoffRetry retry;
    CuratorFramework client;

    @Before
    public void init() {
        retry = new ExponentialBackoffRetry(1000, 3);
    }


    /**
     * 向zk写入持久化节点,如果节点已存在则会抛异常.
     * PERSISTENT：持久化节点
     * PERSISTENT_SEQUENTIAL：持久化顺序节点
     * EPHEMERAL：临时节点
     * EPHEMERAL_SEQUENTIAL：临时顺序节点
     * @throws Exception
     */
    @Test
    public void testCreateNode() throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("test-zk1-data-idc.yangtuojia.com:2181")
                .connectionTimeoutMs(10000)
                .sessionTimeoutMs(10000)
                .retryPolicy(retry)
                .build();

        client.start();
        String path="/tmp/hb-node2";



        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path, "hb-ok2".getBytes(StandardCharsets.UTF_8));
        client.close();
    }


    /**
     * 从zk持久化节点中读取内容
     * @throws Exception
     */
    @Test
    public void testReadNode() throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("test-zk1-data-idc.yangtuojia.com:2181")
                .connectionTimeoutMs(10000)
                .sessionTimeoutMs(10000)
                .retryPolicy(retry)
                .build();

        client.start();
        String path="/tmp/hb-node1";
        byte[] bytes = client.getData().forPath(path);
        System.out.println(new String(bytes));

        // 得到父节点下有哪些子节点
        List<String> list = client.getChildren().forPath("/tmp");
        System.out.println(list);
        client.close();
    }


    /**
     * 设置node数据
     * @throws Exception
     */
    @Test
    public void testSetNode() throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("test-zk1-data-idc.yangtuojia.com:2181")
                .connectionTimeoutMs(10000)
                .sessionTimeoutMs(10000)
                .retryPolicy(retry)
                .build();

        client.start();
        String path = "/tmp/hb-node1";
        client.setData().forPath(path, "hb-ok-ok2".getBytes(StandardCharsets.UTF_8));

        client.close();
    }

    /**
     * 监听node节点变更测试
     * 实例化Watcher，并实现process方法，当节点发生变化，会执行process方法，使用usingWatcher方法添加监听方法。
     * @throws Exception
     */
    @Test
    public void testWatchNode() throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("test-zk1-data-idc.yangtuojia.com:2181")
                .connectionTimeoutMs(10000)
                .sessionTimeoutMs(10000)
                .retryPolicy(retry)
                .build();
        client.start();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Watcher w = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("监听到的变化 watchedEvent= " + watchedEvent);
                countDownLatch.countDown();
            }
        };

        client.getData().usingWatcher(w).forPath("/tmp/hb-node1");
        countDownLatch.await();
        client.close();
    }


}
