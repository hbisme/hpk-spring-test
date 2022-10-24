package priv.hb.sample.sql.druid.common.pojo;

import java.util.ArrayList;
import java.util.List;


import io.vavr.Tuple2;
import lombok.Data;

import static io.vavr.API.Tuple;

/**
 * @author hubin
 * @date 2022年09月21日 17:10
 */
@Data
public class Target {
    private String dbName;
    private String tableName;
    private List<Tuple2<String, String>> partitions = new ArrayList<>();

    public Target(String dbName, String tableName) {
        this.dbName = dbName;
        this.tableName = tableName;
    }

    public static Target of(String dbName, String tableName) {
        return new Target(dbName, tableName);
    }

    public void addPartitions(String key, String value) {
        partitions.add(Tuple(key, value));
    }


}
