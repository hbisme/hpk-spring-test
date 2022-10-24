package priv.hb.sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;

/**
 * 认证的测试
 * @author hubin
 * @date 2022年03月10日 5:00 下午
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @GetMapping("doLogin")
    public String doLogin(@RequestParam String name, @RequestParam String password) {
        // 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对
        if ("hb".equals(name) && "123".equals(password)) {
            StpUtil.login(101);
            return "登录成功";
        } else {
            return "登录失败";
        }
    }

    @GetMapping("isLogin")
    public String isLogin() {
        return "当前会话是否登录: " + StpUtil.isLogin();
    }


    @GetMapping("logout")
    public String logout() {
        StpUtil.logout();
        return "退出登录成功";
    }

    @GetMapping("info")
    public String getInfo() {
        StpUtil.checkLogin();
        int userId = StpUtil.getLoginIdAsInt();
        final SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return "用户: " + userId + "的 token 是: " + tokenInfo;
    }

    @GetMapping("hello")
    public String hello() {
        return "不需要登录就能访问的页面";
    }


}
