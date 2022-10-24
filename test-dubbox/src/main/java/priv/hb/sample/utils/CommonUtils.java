package priv.hb.sample.utils;

import priv.hb.sample.service.UserService;

import static priv.hb.sample.utils.SpringContentUtil.getBean;

public class CommonUtils {

    public static String getTest0() {
        return getBean(UserService.class).test0();
    }
}
