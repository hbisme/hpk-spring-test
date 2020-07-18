package com.hb.config;

import com.yangt.ucenter.sso.client.enums.SsoRunModeEnum;
import com.yangt.ucenter.sso.client.filter.AdminSSOFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
@ConfigurationProperties(prefix = "sso")
public class SsoClientConfig {
    private String runMode;

    private String appCode;

    private String appType;

    private String loginUrl;

    private String loginChecktime;

    private String errorUrl;

    private String excludedPages;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Bean
    public com.yangt.ucenter.sso.client.config.SsoClientConfig ssoClientg() {
        com.yangt.ucenter.sso.client.config.SsoClientConfig ssoClientConfig = new com.yangt.ucenter.sso.client.config.SsoClientConfig();
        ssoClientConfig.setRunMode(SsoRunModeEnum.valueOf(runMode));
        ssoClientConfig.setAppCode(appCode);
        ssoClientConfig.setAppType(appType);
        ssoClientConfig.setLoginUrl(loginUrl);
        ssoClientConfig.setIsOpenLoginTime(loginChecktime);
        ssoClientConfig.setErrorUrl(errorUrl);
        return ssoClientConfig;
    }

    @Bean
    public FilterRegistrationBean ssoFilterRegistration() {
        /*
         * ServletFilter 初始化时机较早，其依赖的 Bean 尚未注入，需手动注入 参考：<a href=
         * "http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/html/howto-embedded-servlet-containers.html"
         * ></a>
         */

        beanFactory.autowireBean(ssoClientg());
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        AdminSSOFilter ssoFilter = new AdminSSOFilter();
        registrationBean.setFilter(ssoFilter);
        ArrayList<Object> urlPatterns = new ArrayList<>();
        // urlPatterns.add("/*");
        registrationBean.setUrlPatterns(urlPatterns);
        registrationBean.addInitParameter("excludedPages", this.excludedPages);
        return registrationBean;

    }


    public String getRunMode() {
        return runMode;
    }

    public void setRunMode(String runMode) {
        this.runMode = runMode;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLoginChecktime() {
        return loginChecktime;
    }

    public void setLoginChecktime(String loginChecktime) {
        this.loginChecktime = loginChecktime;
    }

    public String getErrorUrl() {
        return errorUrl;
    }

    public void setErrorUrl(String errorUrl) {
        this.errorUrl = errorUrl;
    }

    public String getExcludedPages() {
        return excludedPages;
    }

    public void setExcludedPages(String excludedPages) {
        this.excludedPages = excludedPages;
    }
}
