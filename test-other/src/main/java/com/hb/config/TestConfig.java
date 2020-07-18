package com.hb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class TestConfig {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    public String echo1() {
        return beanFactory.toString();
    }

}
