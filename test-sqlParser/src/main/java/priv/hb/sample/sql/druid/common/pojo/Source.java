package priv.hb.sample.sql.druid.common.pojo;

import lombok.Data;

/**
 * @author hubin
 * @date 2022年09月21日 17:09
 */
@Data
public class Source {
    private String dbName;
    private String tableName;

    public Source(String dbName, String tableName) {
        this.dbName = dbName;
        this.tableName = tableName;
    }

    public static Source of(String dbName, String tableName) {
        return new Source(dbName, tableName);
    }
}
