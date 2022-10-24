package priv.hb.sample.common;

import priv.hb.sample.dto.Person;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 测试BeanDefinitionRegistryPostProcessor进行后置处理 注入bean
 *
 * @author hubin
 * @date 2022年09月27日 17:20
 */
public class PostProcessorTest {
    @Test
    public void test1() {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
        MyBeanDefinitionRegistryPostProcessor beanDefinitionRegistryPostProcessor = new MyBeanDefinitionRegistryPostProcessor();
        annotationConfigApplicationContext.addBeanFactoryPostProcessor(beanDefinitionRegistryPostProcessor);
        annotationConfigApplicationContext.refresh();


        Person person = annotationConfigApplicationContext.getBean(Person.class);
        person.echo();
    }

}
