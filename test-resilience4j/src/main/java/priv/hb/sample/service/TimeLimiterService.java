package priv.hb.sample.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hubin
 * @date 2022年08月12日 11:02
 */
@Service
@Slf4j
public class TimeLimiterService {

    @Bulkhead(name = "backendD", type = Bulkhead.Type.THREADPOOL)
    @TimeLimiter(name = "backendF", fallbackMethod = "getUserFallback")
    public CompletableFuture<String> getUser0(Integer id) throws InterruptedException {
        log.info("[getUser][id({})]", id);
        Thread.sleep(10 * 1000L); // sleep 10 秒
        return CompletableFuture.completedFuture("User:" + id);
    }

    public CompletableFuture<String> getUserFallback(Integer id, Throwable throwable) {
        log.info("[getUserFallback][id({}) exception({})]", id, throwable.getClass().getSimpleName());
        return CompletableFuture.completedFuture("mock:User:" + id);
    }

}