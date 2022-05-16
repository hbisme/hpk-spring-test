package com.hb.service;

import com.hb.bo.UserBo;
import com.hb.dto.UserDto;
import com.hb.utils.UserConvert;
import com.hb.vo.UserVo;

import org.junit.Test;

/**
 * @author hubin
 * @date 2022年03月10日 10:13 上午
 */
public class UserConvertTest {
    @Test
    public void test1() {
        UserDto userDto = new UserDto("hb", "123456");
        UserVo userVo = UserConvert.INSTANCE.convertToVo(userDto);
        System.out.println(userVo);

        final UserBo userBo = UserConvert.INSTANCE.convertToBo(userDto);
        System.out.println(userBo);


    }
}
