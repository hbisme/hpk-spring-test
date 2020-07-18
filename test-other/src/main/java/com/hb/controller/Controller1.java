package com.hb.controller;

import com.hb.config.TestConfig;
import com.hb.service.MyService1;
import com.hb.service.SpringJobBeanFactory;
import com.hb.utils.MyUtils1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller1 {

    @Autowired
    TestConfig testConfig;


    @GetMapping("hello1")
    public String hello1() {
        return "hello11";
    }

    @GetMapping("hello2")
    public String hello2() {
        return testConfig.echo1();
    }

    @GetMapping("hello3")
    public String hello3() {
        MyService1 bean = MyUtils1.echo1();
        System.out.println(bean.echo());
        return "hello3.ok";

    }


}
