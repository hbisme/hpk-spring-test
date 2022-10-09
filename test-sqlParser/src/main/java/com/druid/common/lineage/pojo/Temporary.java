package com.druid.common.lineage.pojo;

import lombok.Data;

/**
 * @author hubin
 * @date 2022年09月22日 09:09
 */
@Data
public class Temporary {

    private String tableName;

    public Temporary(String tableName) {

        this.tableName = tableName;
    }

    public static Temporary of(String tableName) {
        return new Temporary(tableName);
    }
}
