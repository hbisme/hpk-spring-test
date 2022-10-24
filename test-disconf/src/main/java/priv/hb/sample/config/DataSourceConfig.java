package priv.hb.sample.config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

import priv.hb.sample.config.disconf.HiracDataSourceProperties;

import com.yangt.datasource.YtDataSource;
import com.yt.asd.common.mapper.BeanMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;
import javax.sql.DataSource;

@Profile({"dev","pre"})
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.default.dataSourceConfig}")
    private String hiracConfig;


    @Primary
    @Bean(name = "defaultDataSource")
    public DataSource defaultDataSource(HiracDataSourceProperties hiracDataSourceProperties) throws SQLException {
        YtDataSource ytDataSource = new YtDataSource();
        BeanMapper.copy(hiracDataSourceProperties, ytDataSource);
        ytDataSource.setDataConifg(hiracConfig);
        return ytDataSource;
    }

    @Bean
    @Primary
    public ServletRegistrationBean druidStatViewServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        servletRegistrationBean.addInitParameter("loginUsername", "hera");
        servletRegistrationBean.addInitParameter("loginUsername", "hera");
        servletRegistrationBean.addInitParameter("loginPassword", "admin");
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }

    @Bean
    @Primary
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }



















































}
