package priv.hb.sample;

import priv.hb.sample.common.HotDeployWithSpring;
import priv.hb.sample.service.Calculator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author hubin
 * @date 2022年06月13日 17:43
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class Test1 {
    @Autowired
    HotDeployWithSpring hotDeployWithSpring;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 测试从jar包加载bean
     *
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        hotDeployWithSpring.hotDeployWithSpring();
    }

    /**
     * 测试销毁bean
     *
     * @throws Exception
     */
    @Test
    public void test2() throws Exception {
        hotDeployWithSpring.delete();
    }


    /**
     * 测试销毁后,再获得bean, 结果和预期一样,获得不到
     *
     * @throws Exception
     */
    @Test
    public void test3() throws Exception {
        hotDeployWithSpring.delete();
        Calculator calculator = (Calculator) applicationContext.getBean("calculator2Impl");
        System.out.println(calculator.add(2, 3));

    }

}
