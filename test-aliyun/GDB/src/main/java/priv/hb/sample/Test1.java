package priv.hb.sample;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ResultSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class Test1 {
    public static void main(String[] args) throws FileNotFoundException {
        // String yaml = args[0];
        String yaml = "/Users/hubin/work/ideaProject/myIdeaProjects/hpk-spring-test/test-aliyun/GDB/src/main/resources/gdb-remote.yaml";

        // 1. 初始化客户端，客户端包含连接池，线程安全，支持多线程并发
        Cluster cluster = Cluster.build(new File(yaml)).create();
        Client client = cluster.connect().init();

        // 2. 发送gremlin请求到GDB服务端，根据业务逻辑定制
        // String dsl = "g.addV(yourlabel).property(propertyKey, propertyValue)";
        // HashMap<String, Object> paramters = new HashMap<String, Object>();
        // paramters.put("yourlabel", "area");
        // paramters.put("propertyKey", "wherence");
        // paramters.put("propertyValue", "hb");
        //
        // ResultSet results = client.submit(dsl, paramters);
        // List<Result> result = results.all().join();
        //
        // for (Result result1 : result) {
        //     System.out.println(result1.getObject());
        // }


        String dsl2 = "g.V().hasLabel('yourlabel')";
        ResultSet results = client.submit(dsl2);
        List<Result> result = results.all().join();
        for (Result result1 : result) {
            System.out.println(result);
        }


        cluster.close();
    }
}
