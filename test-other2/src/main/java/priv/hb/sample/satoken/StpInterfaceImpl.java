package priv.hb.sample.satoken;

import org.springframework.stereotype.Component;

import java.util.List;

import cn.dev33.satoken.stp.StpInterface;

/**
 * 用户权限和角色.
 * @author hubin
 * @date 2022年03月10日 5:26 下午
 */
@Component
public class StpInterfaceImpl implements StpInterface {
    // public static List<String> permissionList = io.vavr.collection.List.of("user-add", "user-update", "user-get").toJavaList();
    // public static List<String> roleList = io.vavr.collection.List.of("admin", "guest").toJavaList();

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return io.vavr.collection.List.of("user-add", "user-update", "user-get").toJavaList();
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return io.vavr.collection.List.of("admin", "guest").toJavaList();
    }
}
