package priv.hb.sample.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 将bean 注入到 静态字段中.
 */
@Component
public class SpringJobBeanFactory implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringJobBeanFactory.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static<T> T getBean(String name) throws BeansException {
        if(applicationContext == null) {
            return null;
        }
        return  (T) applicationContext.getBean(name);
    }
}
