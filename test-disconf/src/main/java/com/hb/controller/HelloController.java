package com.hb.controller;

import com.hb.config.disconf.DefaultDataSourceProperties;
import com.hb.config.disconf.MyTestProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@RestController
public class HelloController {

    @Autowired
    DefaultDataSourceProperties defaultDataSourceProperties;

    @Autowired
    MyTestProperties myTestProperties;

    @Autowired
    DataSource dataSource;

    @GetMapping("/hello")
    public String echoHello() {
        return "hello";
    }

    @GetMapping("/test2")
    public String test2() {
        System.out.println(defaultDataSourceProperties.getName());
        System.out.println(defaultDataSourceProperties.getDriverClassName());
        return defaultDataSourceProperties.toString();
    }

    @GetMapping("/test3")
    public String test3() {
        System.out.println(dataSource);
        return  dataSource.toString();
    }

    @GetMapping("/test4")
    public String test4() {
        System.out.println(myTestProperties);
        return myTestProperties.toString();
    }
}
