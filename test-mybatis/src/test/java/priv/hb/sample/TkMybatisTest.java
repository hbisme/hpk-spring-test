package priv.hb.sample;

import priv.hb.sample.dao.entity.StreamTaskDO;
import priv.hb.sample.service.StreamTaskQueryService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 测试tkMybatis
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TkMybatisTest {
    @Autowired
    StreamTaskQueryService streamTaskQueryService;

    /**
     * 测试tk,按原版mybatis写法来写xml的情况
     */
    @Test
    public void test1() {
        StreamTaskDO res = streamTaskQueryService.selectByJobId(60L);
        System.out.println(res);
    }

    /**
     * 测试tk, 不写mapper.xml来执行sql的情况.
     */
    @Test
    public void testNoXmlSelectByPrimaryKey() {
        StreamTaskDO res = streamTaskQueryService.selectByPrimaryKey(33L);
        System.out.println(res);
    }

    @Test
    public void test() {
        List<StreamTaskDO> res = streamTaskQueryService.selectByExample(33L, "");
        System.out.println(res);
    }


}
