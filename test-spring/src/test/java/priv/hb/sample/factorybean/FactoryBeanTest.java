package priv.hb.sample.factorybean;

import priv.hb.sample.dto.Person;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author hubin
 * @date 2022年09月27日 17:14
 */
public class FactoryBeanTest {

    /**
     * 测试factoryBean来创建bean
     */
    @Test
    public void test1() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MyConfigure2.class);
        Person person = applicationContext.getBean(Person.class);
        person.echo();

    }
}
