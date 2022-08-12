package com.hb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

/**
 * 熔断器测试
 * @author hubin
 * @date 2022年08月12日 09:52
 */
@RestController
@RequestMapping("/break-demo")
@Slf4j
public class BreakerController {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 打开/关闭注释用于测试熔断器开启/关闭
     *
     */
    // @GetMapping("/get")
    // public String get(@RequestParam("id") Integer id) {
    //     return "id: " + id;
    // }


    @GetMapping("/get_user")
    @CircuitBreaker(name = "backendA", fallbackMethod = "getUserFallback")
    public String getUser(@RequestParam("id") Integer id) {
        log.info("[getUser][准备调用 user-service 获取用户({})详情]", id);
        // return restTemplate.getForEntity("http://127.0.0.1:8080/user/get?id=" + id, String.class).getBody();
        return restTemplate.getForEntity("http://127.0.0.1:8080/break-demo/get?id=" + id, String.class).getBody();
    }

    public String getUserFallback(Integer id, Throwable throwable) {
        log.info("[getUserFallback][id({}) exception({})]", id, throwable.getClass().getSimpleName());
        return "mock:User:" + id;
    }

}
