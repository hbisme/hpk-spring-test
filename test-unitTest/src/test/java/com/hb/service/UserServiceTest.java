package com.hb.service;

import com.hb.dao.UserDao;
import com.hb.dataobject.UserDO;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * service层测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    /**
     * 在类型为 UserDao的 属性上，添加了 @MockBean 注解，创建了一个基于 Mockito 的 UserDao Mock 代理对象 Bean。
     * 同时，该 Bean 会注入到依赖 UserDao 的 UserService 中。
     * 这样，我们就可以 mock UserService 的方法，实现对 UserService 的单元测试。
     */
    @MockBean
    private UserDao userDao;


    @Autowired
    private UserService userService;

    @Test
    public void testGet() {
        Mockito.when(userDao.selectById(100)).thenReturn(
                new UserDO().setUsername("hb").setId(100)
        );

        UserDO userDO = userService.get(100);
        System.out.println(userDO);

        Assert.assertEquals("编号不匹配", 100, (int) userDO.getId());
        Assert.assertEquals("用户名不匹配", "hb", userDO.getUsername());

    }



}
