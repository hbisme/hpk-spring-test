package priv.hb.sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 权限判断测试,判断用户是否有某些权限或者某些角色
 * @author hubin
 * @date 2022年03月10日 5:33 下午
 */
@RestController()
@RequestMapping("/auth")
public class AuthController {

    /**
     * 权限认证
     *
     */
    @GetMapping("authInfo")
    public String authInfo() {
        final boolean hasGet = StpUtil.hasPermission("user-get");
        final boolean hasDelete = StpUtil.hasPermission("user-delete");
        return "用户是否有查询权限: " + hasGet + "用户是否有删除权限: " + hasDelete;
    }

    /**
     * 角色认证
     */
    @GetMapping("roleInfo")
    public String roleInfo() {
        final boolean admin = StpUtil.hasRole("admin");
        final boolean superAdmin = StpUtil.hasRole("super-admin");
        return "是否有普通管理员权限: " + admin + "用户是否有超级管理员权限: " + superAdmin;

    }


}
