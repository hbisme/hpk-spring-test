package com.hb.dto;

import lombok.Data;

/**
 * @author hubin
 * @date 2022年09月27日 16:38
 */
@Data
public class Person {
    String name;

    String address;

    public void echo() {
        System.out.println("person is ok");
    }

}
