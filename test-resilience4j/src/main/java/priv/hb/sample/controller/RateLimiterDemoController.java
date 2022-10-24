package priv.hb.sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;

/**
 * 限流测试
 *
 * @author hubin
 * @date 2022年08月12日 10:15
 */
@RestController
@RequestMapping("/rate-limiter-demo")
@Slf4j
public class RateLimiterDemoController {

    /**
     * fallbackMethod 属性对应的 fallback 方法，不仅仅处理被限流时抛出的 RequestNotPermitted 异常，也处理方法执行时的普通异常。
     */
    @GetMapping("/get_user")
    @RateLimiter(name = "backendB", fallbackMethod = "getUserFallback")
    public String getUser(@RequestParam("id") Integer id) {
        log.info("进行限流接口,请求id: " + id);
        // int i = 1/0; // fallbackMethod 也会处理普通异常
        return "User:" + id;
    }

    public String getUserFallback(Integer id, Throwable throwable) {
        log.info("[getUserFallback][id({}) exception({})]", id, throwable.getClass().getSimpleName());
        return "mock:User:" + id;
    }

}
