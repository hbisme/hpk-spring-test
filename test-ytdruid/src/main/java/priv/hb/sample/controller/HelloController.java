package priv.hb.sample.controller;

import priv.hb.sample.config.disconfig.DefaultDataSourceProperties;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@RestController
public class HelloController {

    @Autowired
    DefaultDataSourceProperties defaultDataSourceProperties;


    @Autowired
    @Qualifier("hiracDataSource")
    DataSource dataSource;

    @GetMapping("/hello")
    public String echoHello() {
        return "hello";
    }

    @GetMapping("/test2")
    public String test2() {
        System.out.println(defaultDataSourceProperties.getName());
        System.out.println(defaultDataSourceProperties.getDriverClassName());
        return defaultDataSourceProperties.toString();
    }

    @GetMapping("/test3")
    public String test3() {
        System.out.println(dataSource);
        return  dataSource.toString();
    }


}
