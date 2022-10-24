package priv.hb.sample.config.disconfig;

import com.hipac.disconf.client.common.annotations.DisconfFile;
import com.hipac.disconf.client.common.annotations.DisconfFileItem;

import org.springframework.stereotype.Service;

@Service
@DisconfFile(filename = "edp.datasource.properties")  // disconf key的名称
public class EdpDataSourceProperties {
    private String name;
    private String type;
    private String driverClassName;
    private String filters;
    private Integer maxActive;
    private int initialSize;
    private int maxWait;
    private int minIdle;
    private int timeBetweenEvictionRunsMillis;
    private int minEvictableIdleTimeMillis;
    private String validationQuery;
    private boolean testWhileIdle;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private boolean poolPreparedStatements;
    private int maxOpenPreparedStatements;


    @DisconfFileItem(name ="name.value", associateField = "name")
    public String getName() {
        return name;
    }

    @DisconfFileItem(name ="type.value", associateField = "type")
    public String getType() {
        return type;
    }

    @DisconfFileItem(name ="driverClassName.value", associateField = "driverClassName")
    public String getDriverClassName() {
        return driverClassName;
    }

    @DisconfFileItem(name ="filters.value", associateField = "filters")
    public String getFilters() {
        return filters;
    }

    @DisconfFileItem(name ="maxActive.value", associateField = "maxActive")
    public Integer getMaxActive() {
        return maxActive;
    }

    @DisconfFileItem(name ="initialSize.value", associateField = "initialSize")
    public int getInitialSize() {
        return initialSize;
    }

    @DisconfFileItem(name ="maxWait.value", associateField = "maxWait")
    public int getMaxWait() {
        return maxWait;
    }

    @DisconfFileItem(name ="minIdle.value", associateField = "minIdle")
    public int getMinIdle() {
        return minIdle;
    }

    @DisconfFileItem(name ="timeBetweenEvictionRunsMillis.value", associateField = "timeBetweenEvictionRunsMillis")
    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    @DisconfFileItem(name ="minEvictableIdleTimeMillis.value", associateField = "minEvictableIdleTimeMillis")
    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    @DisconfFileItem(name ="validationQuery.value", associateField = "validationQuery")
    public String getValidationQuery() {
        return validationQuery;
    }

    @DisconfFileItem(name ="testWhileIdle.value", associateField = "testWhileIdle")
    public boolean getTestWhileIdle() {
        return testWhileIdle;
    }

    @DisconfFileItem(name ="testOnBorrow.value", associateField = "testOnBorrow")
    public boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    @DisconfFileItem(name ="testOnReturn.value", associateField = "testOnReturn")
    public boolean getTestOnReturn() {
        return testOnReturn;
    }

    @DisconfFileItem(name ="poolPreparedStatements.value", associateField = "poolPreparedStatements")
    public boolean getPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    @DisconfFileItem(name ="maxOpenPreparedStatements.value", associateField = "maxOpenPreparedStatements")
    public int getMaxOpenPreparedStatements() {
        return maxOpenPreparedStatements;
    }

}
