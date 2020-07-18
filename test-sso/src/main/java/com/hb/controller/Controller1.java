package com.hb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller1 {

    @Autowired
    com.yangt.ucenter.sso.client.config.SsoClientConfig ssoClientg;

    @Autowired
    FilterRegistrationBean ssoFilterRegistration;

    @GetMapping("/hello1")
    public String hello1() {
        return "hello1";
    }

    @GetMapping("/hello2")
    public String hello2() {

        System.out.println(ssoClientg);
        return "hello2";
    }

    @GetMapping("/hello3")
    public String hello3() {

        System.out.println(ssoFilterRegistration);
        return "hello3";
    }


    @GetMapping("/info")
    public String info() {

        System.out.println(ssoFilterRegistration);
        return "hello info";
    }

}
