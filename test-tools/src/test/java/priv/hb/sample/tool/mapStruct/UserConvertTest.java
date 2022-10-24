package priv.hb.sample.tool.mapStruct;



import priv.hb.sample.tool.mapStruct.mapper.UserConvert;
import priv.hb.sample.tool.mapStruct.pojo.UserBo;
import priv.hb.sample.tool.mapStruct.pojo.UserDto;
import priv.hb.sample.tool.mapStruct.pojo.UserVo;


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
