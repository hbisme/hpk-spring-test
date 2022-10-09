package com.druid.common.lineage.pojo;

import lombok.Data;

/**
 * @author hubin
 * @date 2022年09月21日 17:10
 */
@Data
public class Target {
    private String dbName;
    private String tableName;

    public Target(String dbName, String tableName) {
        this.dbName = dbName;
        this.tableName = tableName;
    }

    public static Target of(String dbName, String tableName) {
        return new Target(dbName, tableName);
    }
}
