package com.hb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

/**
 * 重试测试
 * 通过 fallbackMethod 属性，设置执行发生 Exception 异常时，执行对应的 #getUserFallback(Integer id, Throwable throwable) 方法。
 * 注意，fallbackMethod 方法的参数要和原始方法一致，最后一个为 Throwable 异常。
 * @author hubin
 * @date 2022年08月12日 10:33
 */
@RestController
@RequestMapping("/retry-demo")
@Slf4j
public class RetryDemoController {
    @Autowired
    private RestTemplate restTemplate;

    public void random() {
        double r = Math.random();
        if (r < 0.5) {
            throw new RuntimeException("概率抛出异常");
        }
    }


    @GetMapping("/get_user")
    @Retry(name = "backendE", fallbackMethod = "getUserFallback")
    public String getUser(@RequestParam("id") Integer id) {
        log.info("[getUser][准备调用 user-service 获取用户({})详情]", id);
        random();
        return "user: " + id;
    }

    public String getUserFallback(Integer id, Throwable throwable) {
        log.info("[getUserFallback][id({}) exception({})]", id, throwable.getClass().getSimpleName());
        return "mock:User:" + id;
    }



}
