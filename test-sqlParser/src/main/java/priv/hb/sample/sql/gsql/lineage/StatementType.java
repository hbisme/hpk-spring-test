package priv.hb.sample.sql.gsql.lineage;

import lombok.Getter;

@Getter
public enum StatementType {

    INSERT_INTO_VALUES("insert_into_values"),
    INSERT_OVERWRITE_TABLE("insert_overwrite_table"),
    INSERT_OVERWRITE_DIRECTORY("insert_overwrite_directory"),
    INSERT_INTO_SELECT("insert_into_select"),

    ADD_PARTITION("add_partition"),
    DROP_PARTITION("drop_partition"),
    CREATE_TABLE_AS_SELECT("create_table_as_select"),
    DROP_TABLE("drop_table"),
    DELETE_FROM("delete_from"),
    ALTER_TABLE("alter_table"),
    TRUNCATE("truncate_table"),


    CREATE_VIEW("create_view"),
    CREATE_OR_REPLACE_VIEW("create_or_replace_view"),
    DROP_VIEW("drop_view"),
    ALTER_VIEW("alter_view"),

    UPDATE_FROM("update_from"),
    SELECT_FROM("select_from"),

    LOAD_DATA_INPATH_OVERWRITE("load_data_inpath_overwrite"),
    LOAD_DATA_LOCAL_INPATH_OVERWRITE("load_data_local_inpath_overwrite");

    private String name;

    StatementType(String name) {
        this.name = name;
    }
}
