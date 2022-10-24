package priv.hb.sample.sql.gsql.lineage;

import lombok.AllArgsConstructor;
import lombok.Data;

import static java.lang.String.format;

/**
 * @author hubin
 * @date 2022年09月30日 09:41
 */
@Data
@AllArgsConstructor
public class LineageTarget {
    private Long id;
    private Long lineageId;
    private String key;
    /**
     * table, directory
     */
    private String targetType;
    /**
     * 数据源类型，mysql, es, hive, clickhouse
     */
    private String sourceType;
    /**
     * 语句类型，一般是SQL才会有该值 insert, update, delete, alter, drop, truncate, use, select, load, create
     */
    private String stmtType;
    /**
     * 一般是sqoop连接mysql，或者hive beeline，或者是datax
     */
    private String jdbcUrl;
    /**
     * insert_overwrite,load_data,insert_into,insert_dir
     */
    private String opType;
    /**
     * 库名
     */
    private String dbName;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 表 partition
     */
    private String partition;
    /**
     * 动态分区选项
     */
    private String dynamicPartition;
    /**
     * 输出路径
     */
    private String path;

    public static LineageTarget empty() {
        return new LineageTarget(null,null,"empty#" + System.currentTimeMillis(), "empty", null, null, null,null,null, null, null, null, null);
    }

    public static LineageTarget general(String targetName) {
        return new LineageTarget(null,null, null, "empty", null, null, null, null,null, targetName, null, null, null);

    }
    public static LineageTarget select(String sourceType, StatementType stmtType) {
        return new LineageTarget(null,null, format("empty#%s", sourceType), "empty", sourceType, stmtType.getName(), null, stmtType.getName(),null, null, null, null, null);
    }
    public static LineageTarget insertDirectory(String sourceType, StatementType stmtType, String directory) {
        return new LineageTarget(null,null, format("directory#%s", directory), "directory", sourceType, stmtType.getName(), null, stmtType.getName(), null, null, null, null, directory);
    }
    public static LineageTarget insertTable(String sourceType, StatementType stmtType, String database, String name, String partition) {
        return new LineageTarget(null,null, format("insert#%s#%s", database, name),"table", sourceType, stmtType.getName(), null, stmtType.getName(), database, name, partition, null, null);
    }
    public static LineageTarget insertTable(String sourceType, StatementType stmtType, String database, String name, String partition, String partitionOption) {
        return new LineageTarget(null,null, format("insert#%s#%s", database, name),"table", sourceType, stmtType.getName(), null, stmtType.getName(), database, name, partition, partitionOption, null);
    }
    public static LineageTarget createView(String sourceType, String database, String name) {
        return new LineageTarget(null,null, format("createView#%s#%s", database, name),"view", sourceType, "create_view", null, StatementType.CREATE_VIEW.getName(), database, name, null, null, null);
    }
    public static LineageTarget addPartition(String sourceType, String database, String name, String partition) {
        return new LineageTarget(null,null, format("update#%s#%s", database, name),"table", sourceType, StatementType.ADD_PARTITION.getName(),"update", StatementType.ADD_PARTITION.getName(), database, name, partition, null, null);
    }
    public static LineageTarget dropPartition(String sourceType, String database, String name, String partition) {
        return new LineageTarget(null,null, format("update#%s#%s", database, name),"table", sourceType, StatementType.DROP_PARTITION.getName(), null, StatementType.DROP_PARTITION.getName(), database, name, partition, null, null);
    }
    public static LineageTarget loadData(String sourceType, StatementType stmtType, String database, String name, String partition) {
        return new LineageTarget(null,null,format("insert#%s#%s", database, name),"table", sourceType, stmtType.getName(), null, stmtType.getName(), database, name, partition, null, null);
    }
    public static LineageTarget update(String sourceType, String database, String name, String partition) {
        return new LineageTarget(null,null, format("update#%s#%s", database, name),"table", sourceType,"update", null, StatementType.UPDATE_FROM.getName(), database, name, partition, null, null);
    }
    public static LineageTarget delete(String sourceType, String database, String name, String partition) {
        return new LineageTarget(null,null, format("delete#%s#%s", database, name),"table", sourceType, "delete_from", null, StatementType.DELETE_FROM.getName(), database, name, partition, null, null);
    }
    public static LineageTarget alterTable(String sourceType, String database, String name, String partition) {
        return new LineageTarget(null,null, format("alterTable#%s#%s", database, name), "table", sourceType, "alter_table", null, StatementType.ALTER_TABLE.getName(), database, name, partition, null, null);
    }
    public static LineageTarget dropTable(String sourceType, String database, String name, String partition) {
        return new LineageTarget(null,null, format("dropTable#%s#%s", database, name), "table", sourceType, "drop_table", null, StatementType.DROP_TABLE.getName(), database, name, partition, null, null);
    }
    public static LineageTarget alterView(String sourceType, String database, String name) {
        return new LineageTarget(null,null, format("alterView#%s#%s", database, name), "view", sourceType, "alter_view", null, StatementType.ALTER_VIEW.getName(), database, name, null, null, null);
    }
    public static LineageTarget truncate(String sourceType, String database, String name) {
        return new LineageTarget(null,null, format("truncate#%s#%s", database, name), "table", sourceType, "truncate", null, StatementType.TRUNCATE.getName(), database, name, null, null, null);
    }
    public  static LineageTarget createTableAsSelect(String sourceType, StatementType createTableAsSelect, String database, String name, String partition) {
        return new LineageTarget(null,null, format("createTableAsSelect#%s#%s", database, name),"table",sourceType,"create_table_as_select",null, StatementType.CREATE_TABLE_AS_SELECT.getName(), database, name, partition, null,null);
    }
    public  static LineageTarget createOrReplaceView(String sourceType, StatementType stmtType, String database, String name) {
        return new LineageTarget(null,null, format("createOrReplaceView#%s#%s", database, name),"view",sourceType,"create_or_replace_view",null, StatementType.CREATE_OR_REPLACE_VIEW.getName(), database, name, null, null,null);
    }


}
