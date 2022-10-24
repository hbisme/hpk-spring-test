package priv.hb.sample.utils;

import priv.hb.sample.service.MyService1;
import priv.hb.sample.service.SpringJobBeanFactory;

public class MyUtils1 {
    public static MyService1 getBean() {
        MyService1 bean = SpringJobBeanFactory.getBean("myService1");
        return bean;
    }
}
