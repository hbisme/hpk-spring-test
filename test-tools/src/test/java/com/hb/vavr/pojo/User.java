package com.hb.vavr.pojo;

import lombok.Data;

/**
 * @author hubin
 * @date 2022年10月14日 11:21
 */
@Data
public class User {

    private int id;
    private String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
