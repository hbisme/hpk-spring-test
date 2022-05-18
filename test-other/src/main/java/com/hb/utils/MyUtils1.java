package com.hb.utils;

import com.hb.service.MyService1;
import com.hb.service.SpringJobBeanFactory;

public class MyUtils1 {
    public static MyService1 getBean() {
        MyService1 bean = SpringJobBeanFactory.getBean("myService1");
        return bean;
    }
}
