package com.hb.controller;

import com.hb.config.TestConfig;
import com.hb.utils.MyUtils1;
import com.hb.service.MyService1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller1 {

    @Autowired
    TestConfig testConfig;



    @GetMapping("/hello1")
    public String hello1() throws InterruptedException {
        Thread.sleep(10000);
        return "hello11";
    }

    @GetMapping("/hello2")
    public String hello2() {
        return testConfig.getAutowireBeanFactory();
    }

    @GetMapping("/hello3")
    public String hello3() {
        MyService1 bean = MyUtils1.getBean();
        System.out.println(bean.echo());
        return "hello3.ok";
    }

    @GetMapping("/hello4")
    public String hello4(@RequestParam(required = false) Boolean f1, @RequestParam(required = false) Boolean f2) {

        if (f1 == null) {
            System.out.println("f1 is null ");
        }

        if (f2 == null) {
            System.out.println("f2 is null ");
        }
        MyService1 bean = MyUtils1.getBean();
        System.out.println(bean.echo());
        return "hello3.ok";
    }

    @GetMapping("/hello5")
    public String hello5(@RequestParam String name) {
        System.out.println(name);
        return "hello5.ok";
    }

    @PostMapping("/hello6")
    public String hello6(@RequestParam String name) {
        System.out.println("post ->" + name);
        return "hello6.ok";
    }

    @PutMapping("/hello7")
    public String hello7(@RequestParam String name) {
        System.out.println("put ->" + name);
        return "hello7.ok";
    }


    // 测试flink 的Restful接口
    @GetMapping("/get1")
    public void get1(@RequestParam String key, @RequestParam int value) {
        System.out.println("get1 key: " + key + ", value: " + value);
    }

    // @GetMapping("/get2")
    // public void get2(@RequestBody String key, @RequestBody int value) {
    //     System.out.println("get2 key: " + key + ", value: " + value);
    // }

    @PostMapping("/post1")
    public void post1(@RequestParam String key, @RequestParam int value) {
        System.out.println("post1 key: " + key + ", value: " + value);
    }

    @PostMapping("/post2")
    public void post2(MyKV kv) {
        System.out.println("post2 key: " + kv.key + ", value: " + kv.value);
    }

    @PostMapping("/post3")
    public void post3(MyKV kv, @RequestParam String pkey) {
        System.out.println("post3 key: " + kv.key + ", value: " + kv.value + " ,Get key: " + pkey);
    }

    @PostMapping("/post4")
    public void post4(@RequestParam String item_ids, @RequestParam Integer showTimes) {
        System.out.println("post4,  item_ids:" + item_ids + " showtimes: " + showTimes);
    }


    class MyKV {
        private String key;
        private int value;

        public MyKV() {
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
