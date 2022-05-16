package com.hb.dto;

/**
 * @author hubin
 * @date 2022年03月08日 6:05 下午
 */
public class User {
    Integer id;
    String name;

    public User() {
    }

    public User(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
