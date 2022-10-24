package priv.hb.sample.controller;

import priv.hb.sample.config.disconf.HiracDataSourceProperties;
import priv.hb.sample.config.disconf.MyTestProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@RestController
public class HelloController {

    @Autowired
    HiracDataSourceProperties hiracDataSourceProperties;

    @Autowired
    MyTestProperties myTestProperties;

    @Autowired
    DataSource dataSource;

    @GetMapping("/hello")
    public String echoHello() {
        return "hello";
    }

    @GetMapping("/test2")
    public String test2() {
        System.out.println(hiracDataSourceProperties.getName());
        System.out.println(hiracDataSourceProperties.getDriverClassName());
        return hiracDataSourceProperties.toString();
    }

    @GetMapping("/test3")
    public String test3() {
        System.out.println(dataSource);
        return  dataSource.toString();
    }

    @GetMapping("/test4")
    public String test4() {
        System.out.println(myTestProperties);
        return myTestProperties.toString();
    }
}
