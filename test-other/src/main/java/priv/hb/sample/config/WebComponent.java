package priv.hb.sample.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class WebComponent {


    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registraion = new FilterRegistrationBean();
        registraion.setFilter(filterDemo3());
        registraion.addUrlPatterns("/hb1");
        registraion.addInitParameter("paramName", "paramValue");
        registraion.setName("filterDemo3");
        registraion.setOrder(6);
        return registraion;
    }


    @Bean
    public Filter filterDemo3() {
        return new FilterDemo3();
    }

}
