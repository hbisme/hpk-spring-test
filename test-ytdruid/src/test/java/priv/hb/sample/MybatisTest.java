package priv.hb.sample;

import priv.hb.sample.dao.entity.edp.StreamJobDO;
import priv.hb.sample.dao.entity.hirac.HiracActionDO;
import priv.hb.sample.dao.entity.hirac.HiracJobDO;
import priv.hb.sample.service.EdpService;
import priv.hb.sample.service.HiracJobService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 多数据源测试
 * @author hubin
 * @date 2022年04月27日 3:34 下午
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MybatisTest {

    @Autowired
    HiracJobService hiracJobService;

    @Autowired
    EdpService edpService;

    @Test
    public void test1() {
        final HiracJobDO hiracJobDO = hiracJobService.selectById(10L);
        System.out.println("res: " + hiracJobDO);
    }

    @Test
    public void test2() {
        final HiracActionDO hiracActionDO = hiracJobService.selectActionById(202204272330007664L);
        System.out.println(hiracActionDO);
    }

    @Test
    public void test3() {
        final StreamJobDO streamJobDO = edpService.selectByid(60L);
        System.out.println(streamJobDO);
    }

}
