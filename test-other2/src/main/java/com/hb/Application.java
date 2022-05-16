package com.hb;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import cn.dev33.satoken.SaManager;

/**
 * @author hubin
 * @date 2022年03月09日 10:03 上午
 */
@SpringBootApplication
@MapperScan(basePackages = "com.hb.dao.mapper")
// @EnableAspectJAutoProxy(exposeProxy = true)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("启动成功：Sa-Token配置如下：" + SaManager.getConfig());

    }
}
