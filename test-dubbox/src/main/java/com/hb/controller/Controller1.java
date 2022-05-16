package com.hb.controller;

import com.hb.service.UserService;
import com.yt.ustone.domain.ResultData;
import com.yt.ustone.domain.to.BasicUserCacheTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller1 {

    @Autowired
    UserService userService;

    @GetMapping("/hello0")
    public String hello0() {
        return userService.test0();
    }

    @GetMapping("/hello1")
    public String hello1() {
        return "hello1";
    }

    @GetMapping("/hello2")
    public ResultData<BasicUserCacheTO> hello2() {
        ResultData<BasicUserCacheTO> res = userService.test1();
        return res;
    }

    @GetMapping("/hello3")
    public String hello3() {
        return userService.testStatic0();
    }


}
