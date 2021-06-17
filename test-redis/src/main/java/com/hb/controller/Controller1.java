package com.hb.controller;

import com.hb.dto.Persion;
import com.hb.service.Service1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller1 {

    @Autowired
    Service1  service1;

    @GetMapping("test1")
    public String echo() {
        return "ok11";
    }

    @GetMapping("test2")
    public Boolean test2() {
        Boolean res = service1.WriteToRedis();
        return res;
    }

    @GetMapping("test3")
    public String test3() {
        String res = service1.GetFromRedis();
        return res;
    }

    @GetMapping("test4")
    public Boolean test4() throws InterruptedException {
        Boolean res = service1.addToList();
        return res;
    }


}
