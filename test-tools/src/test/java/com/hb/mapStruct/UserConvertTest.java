package com.hb.mapStruct;



import com.hb.mapStruct.mapper.UserConvert;
import com.hb.mapStruct.pojo.UserBo;
import com.hb.mapStruct.pojo.UserDto;
import com.hb.mapStruct.pojo.UserVo;


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
