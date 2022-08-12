package com.hb.controller;

import com.hb.service.TimeLimiterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * 执行限时测试.
 * 这里创建了 TimeLimiterService 的原因是，
 * 这里我们使用 Resilience4j 是基于注解 + AOP的方式，如果直接 this. 方式来调用方法，实际没有走代理，导致 Resilience4j 无法使用 AOP。
 *
 */
@RestController
@RequestMapping("/time-limiter-demo")
public class TimeLimiterDemoController {

    // Resilience4j执行限时在TimeLimiterService的getUser0方法上
    @Autowired
    private TimeLimiterService timeLimiterService;

    @GetMapping("/get_user")
    public String getUser(@RequestParam("id") Integer id) throws ExecutionException, InterruptedException {
        return timeLimiterService.getUser0(id).get();
    }




}
