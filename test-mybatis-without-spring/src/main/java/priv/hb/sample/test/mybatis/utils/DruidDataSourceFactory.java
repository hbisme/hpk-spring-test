package priv.hb.sample.test.mybatis.utils;

import com.alibaba.druid.pool.DruidDataSource;

import org.apache.ibatis.datasource.DataSourceFactory;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * @author hubin
 * @date 2022年03月08日 10:44 上午
 */
public class DruidDataSourceFactory implements DataSourceFactory {

    private Properties props;

    @Override
    public void setProperties(Properties properties) {
        this.props = properties;

    }

    @Override
    public DataSource getDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(this.props.getProperty("driver"));
        druidDataSource.setUrl(this.props.getProperty("url"));
        druidDataSource.setUsername(this.props.getProperty("username"));
        druidDataSource.setPassword(this.props.getProperty("password"));
        // 其他配置可以根据MyBatis主配置文件进行配置

        try {
            druidDataSource.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return druidDataSource;
    }
}
