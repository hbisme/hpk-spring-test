package com.gsql.lineage;

import lombok.AllArgsConstructor;
import lombok.Data;

import static java.lang.String.format;

/**
 * @author hubin
 * @date 2022年09月30日 09:41
 */
@Data
@AllArgsConstructor
public class LineageSource {
    private String id;
    private Long lineageId;
    private String key;
    /**
     * from_table,load_inPath
     */
    private String opType;
    /**
     * 数据源类型，mysql, es, hive, clickhouse
     */
    private String sourceType;
    /**
     * 一般是sqoop连接mysql，或者hive beeline，或者是datax
     */
    private String jdbcUrl;

    // 库名加表名
    private String fullName;

    /**
     * 库名
     */
    private String dbName;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 输入路径
     */
    private String path;

    public static LineageSource table(String sourceType, String fullName) {
        return new LineageSource(null, null, format("from_table#%s", fullName), "from_table", sourceType, null, fullName , null, null, null);
    }



    public static LineageSource table(String sourceType, String dbName, String tableName) {
        return new LineageSource(null, null, format("from_table#%s", tableName), "from_table", sourceType, null, dbName + "." + tableName, dbName, tableName, null);
    }

    public static LineageSource path(String sourceType, String path) {
        return new LineageSource(null, null, format("load_inpath#%s", path), "load_inpath", sourceType, null, null, null, null, path);
    }

}