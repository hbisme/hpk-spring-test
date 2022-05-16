package com.hb.utils;

import com.hb.service.UserService;

import static com.hb.utils.SpringContentUtil.getBean;

public class CommonUtils {

    public static String getTest0() {
        return getBean(UserService.class).test0();
    }
}
