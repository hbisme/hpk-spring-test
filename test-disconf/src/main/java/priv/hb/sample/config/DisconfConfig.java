package priv.hb.sample.config;

import com.hipac.disconf.client.DisconfMgrBean;
import com.hipac.disconf.client.DisconfMgrBeanSecond;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DisconfConfig implements ApplicationContextAware {

    private ApplicationContext ctx;


    @Bean(destroyMethod = "destroy")
    public DisconfMgrBean disconfMgrBean() {
        String profile = ctx.getEnvironment().getActiveProfiles()[0];
        DisconfMgrBean disconfMgrBean = new DisconfMgrBean();
        disconfMgrBean.setScanPackage("com.hb.config.disconf");
        disconfMgrBean.setDisconfFileName("disconf/disconf." + profile + ".properties");
        return disconfMgrBean;
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public DisconfMgrBeanSecond disconfMgrBeanSecond() {
        return new DisconfMgrBeanSecond();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
