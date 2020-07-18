package com.hb.service;

import org.springframework.stereotype.Service;

@Service
public class MyService1 {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String echo() {
        return "echo myService1";
    }


}
