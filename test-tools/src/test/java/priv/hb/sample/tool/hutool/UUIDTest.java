package priv.hb.sample.tool.hutool;

import org.junit.jupiter.api.Test;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * @author hubin
 * @date 2022年10月14日 09:34
 */
public class UUIDTest {
    @Test
    public void testUUID() {
        //生成的UUID是带-的字符串，类似于：a5c8a5e8-df2b-4706-bea4-08d0939410e3
        String uuid = IdUtil.randomUUID();

        //生成的是不带-的字符串，类似于：b17f24ff026d40949c85a24f4f375d42
        String simpleUUID = IdUtil.simpleUUID();

        System.out.println(uuid);
        System.out.println(simpleUUID);

    }

    @Test
    public void testSnowflake() {
        // 参数1为终端ID
        // 参数2为数据中心ID
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();

        System.out.println(id);
    }





}
