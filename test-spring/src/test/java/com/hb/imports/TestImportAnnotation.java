package com.hb.imports;

import com.hb.dto.Person;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 测试import注解
 * @author hubin
 * @date 2022年09月27日 16:37
 */
public class TestImportAnnotation {

    /**
     * 测试使用import 导入 Person的bean
     */
    @Test
    public void test1() {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MyConfigure.class);

        Person person = annotationConfigApplicationContext.getBean(Person.class);
        person.echo();

    }
}
