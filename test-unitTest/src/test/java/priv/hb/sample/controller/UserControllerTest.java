package priv.hb.sample.controller;

import priv.hb.sample.service.UserService;
import priv.hb.sample.dataobject.UserDO;

import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 *
 * controller层的单元测试
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
// @AutoConfigureMockMvc 注解，用于自动化配置稍后注入的 MockMvc Bean 对象 mvc 。在后续的测试中，是通过 mvc 调用后端 API 接口。
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    /**
     * 在类型为 UserService 的 userService 属性上，添加了 @MockBean 注解，创建了一个基于 Mockito 的 UserService Mock 代理对象 Bean。
     * 同时，该 Bean 会注入到依赖 UserService 的 UserController 中。
     * 这样，我们就可以 mock userService 的方法，实现对 UserController 的单元测试。
     */
    @MockBean
    private UserService userService;


    @Test
    public void testGet() throws Exception {
        // 先要mock数据
        Mockito.when(userService.get(100)).thenReturn(
                new UserDO().setId(100).setUsername("hb")
        );

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get("/user/get?id=1"));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk()); // 响应状态码 200


        // 校验响应内容方式二：逐个字段匹配
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("id", IsEqual.equalTo(100)));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("username", IsEqual.equalTo("username:hb")));

    }


}
