package com.hb.dataobject;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户 DO
 */
@Data
@Accessors(chain=true)
public class UserDO {

    /**
     * 用户编号
     */
    private Integer id;
    /**
     * 账号
     */
    private String username;
    /**
     * 密码（明文）
     *
     * ps：生产环境下，千万不要明文噢
     */
    private String password;



}