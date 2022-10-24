package priv.hb.sample.factorybean;

import priv.hb.sample.dto.Person;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author hubin
 * @date 2022年09月27日 17:00
 */
public class PersonFactoryBean implements FactoryBean<Person> {

    @Override
    public Person getObject() throws Exception {
        return new Person();
    }

    @Override
    public Class<?> getObjectType() {
        return Person.class;
    }
}
