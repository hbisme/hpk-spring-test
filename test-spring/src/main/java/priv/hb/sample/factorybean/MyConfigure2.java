package priv.hb.sample.factorybean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hubin
 * @date 2022年09月27日 17:12
 */
@Configuration
public class MyConfigure2 {
    @Bean
    public PersonFactoryBean personFactoryBean() {
        return new PersonFactoryBean();
    }
}
