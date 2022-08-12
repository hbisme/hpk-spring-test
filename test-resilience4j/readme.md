### resilience4j 的测试

参考: https://www.iocoder.cn/Spring-Boot/Resilience4j/?yudao

熔断器测试
限流测试
重试测试
执行超时测试

另外，我们将 @RateLimiter 和 @CircuitBreaker 注解添加在相同方法上，进行组合使用，来实现限流和断路的作用。但是要注意，需要添加 resilience4j.circuitbreaker.instances.<instance_name>.ignoreExceptions=io.github.resilience4j.ratelimiter.RequestNotPermitted 配置项，忽略限流抛出的 RequestNotPermitted 异常，避免触发断路器的熔断。

