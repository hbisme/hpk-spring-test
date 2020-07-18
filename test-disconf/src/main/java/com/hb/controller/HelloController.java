package com.hb.controller;

import com.hb.config.disconf.DefaultDataSourceProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@RestController
public class HelloController {

    @Autowired
    DefaultDataSourceProperties defaultDataSourceProperties;

    @Autowired
    DataSource dataSource;

    @GetMapping("/hello")
    public String echoHello() {
        return "hello";
    }

    @GetMapping("/test2")
    public String test2() {
        System.out.println(defaultDataSourceProperties.getName());
        return defaultDataSourceProperties.toString();
    }

    @GetMapping("/test3")
    public String test3() {

        System.out.println(dataSource);

        return  dataSource.toString();

    }
}
