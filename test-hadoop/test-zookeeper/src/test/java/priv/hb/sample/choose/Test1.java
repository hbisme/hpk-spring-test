package priv.hb.sample.choose;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Before;
import org.junit.Test;


import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author hubin
 * @date 2022年11月04日 09:23
 */
public class Test1 {

    ExponentialBackoffRetry retry;
    CuratorFramework client;
    String testEnvZKAddress = "test-zk1-data-idc.yangtuojia.com:2181";
    String primaryPath = "/tmp/choose/primary";

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
     *
     * @throws Exception
     */
    @Test
    public void testCreateNode() throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(testEnvZKAddress)
                .connectionTimeoutMs(10000)
                .sessionTimeoutMs(10000)
                .retryPolicy(retry)
                .build();

        client.start();
        String path = primaryPath;


        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath(path, "ip-1".getBytes(StandardCharsets.UTF_8));
        client.close();


        // Thread.sleep("100000");
    }


    @Test
    public void testReadNode() throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(testEnvZKAddress)
                .connectionTimeoutMs(10000)
                .sessionTimeoutMs(10000)
                .retryPolicy(retry)
                .build();

        client.start();
        String path = primaryPath;
        byte[] bytes = client.getData().forPath(path);
        System.out.println(new String(bytes));


    }
}
