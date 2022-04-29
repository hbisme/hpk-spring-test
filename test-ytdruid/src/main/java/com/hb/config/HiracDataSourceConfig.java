package com.hb.config;


import com.github.pagehelper.PageHelper;
import com.hb.config.disconfig.HiracDataSourceProperties;
import com.yangt.datasource.YtDataSource;
import com.yt.asd.common.mapper.BeanMapper;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author hubin
 * @date 2022年04月27日 2:26 下午
 */
@Configuration
@EnableTransactionManagement
// 多数据源要加这个注解
@MapperScan(basePackages = "com.hb.dao.mapper.hirac", sqlSessionTemplateRef = "hiracSqlSessionTemplate")
public class HiracDataSourceConfig {
    @Value("${spring.datasource.hirac.dataSourceConfig}")
    private String hiracConfig;


    @Bean(name = "hiracDataSource")
    public DataSource hiracDataSource(HiracDataSourceProperties hiracDataSourceProperties) throws SQLException {
        YtDataSource ytDataSource = new YtDataSource();
        ytDataSource.setMinEvictableIdleTimeMillis(31000);
        BeanMapper.copy(hiracDataSourceProperties, ytDataSource);
        ytDataSource.setDataConifg(hiracConfig);
        return ytDataSource;
    }

    @Bean(name = "hiracSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("hiracDataSource") DataSource ds) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(ds);

        // 开启数据表下划线列名 到 实体类驼峰字段的映射
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        bean.setConfiguration(configuration);
        bean.setTypeAliasesPackage("com.hb.dao.hirac");

        // bean.setTypeHandlersPackage("com.yangt.octopus.typehandler");

        //分页插件
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("params", "count=countSql");
        pageHelper.setProperties(properties);

        //添加插件
        bean.setPlugins(new Interceptor[]{pageHelper});

        //添加XML目录
        // ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // bean.setMapperLocations(resolver.getResources("classpath:com/hb/dao/mapper/hirac/*.xml"));

        return bean.getObject();
    }


    @Bean(name = "hiracTransactionManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("hiracDataSource") DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }

    @Bean(name = "hiracSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("hiracSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    // @Bean(name = "hiracJdbcTemplate")
    // public JdbcTemplate jdbcTemplate(@Qualifier("hiracDataSource") DataSource ds) {
    //     return new JdbcTemplate(ds);
    // }


}
