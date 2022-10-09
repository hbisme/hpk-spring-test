package com.hb.common;

import com.hb.dto.User;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 使用xml文件来初始化spring-application的测试
 *
 * @author hubin
 * @date 2022年09月27日 17:39
 */
public class XmlApplicationTest {

    @Test
    public void test1() {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath*:spring-context.xml");
        User user = classPathXmlApplicationContext.getBean(User.class);
        user.echo();


    }
}
