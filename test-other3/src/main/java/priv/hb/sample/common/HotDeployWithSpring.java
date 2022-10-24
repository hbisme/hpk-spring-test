package priv.hb.sample.common;

import priv.hb.sample.service.Calculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

/**
 * @author hubin
 * @date 2022年06月13日 17:38
 */
@Service
public class HotDeployWithSpring {

    private static String jarFile = "/Users/hubin/work/ideaProject/myIdeaProjects/hpk-spring-test/test-other3/target/test-other3-1.0-SNAPSHOT.jar";

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 加入jar包后 动态注册bean到spring容器，包括bean的依赖
     */
    public void  hotDeployWithSpring() throws Exception {
        Set<String> classNameSet = DeployUtils.readJarFile(jarFile);
        URL url = new URL("file:" + jarFile);
        ClassLoader urlClassLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());//自己定义的classLoader类，把外部路径也加到load路径里，使系统去该路经load对象
        System.out.println(url);
        for (String className : classNameSet) {
            Class clazz = urlClassLoader.loadClass(className);
            if (DeployUtils.isSpringBeanClass(clazz)) {
                BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);

                DefaultListableBeanFactory defaultListableBeanFactory = new DefaultListableBeanFactory();
                defaultListableBeanFactory.registerBeanDefinition(DeployUtils.transformName(className), beanDefinitionBuilder.getBeanDefinition());

            }
        }

        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        Calculator calculator = (Calculator) applicationContext.getBean("calculatorImpl");
        System.out.println(calculator.add(1, 3));

    }

    public void delete() throws Exception {
        Set<String> classNameSet = DeployUtils.readJarFile(jarFile);
        URL url = new URL("file:" + jarFile);
        ClassLoader urlClassLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());//自己定义的classLoader类，把外部路径也加到load路径里，使系统去该路经load对象

        for (String className : classNameSet) {
            Class<?> clazz = urlClassLoader.loadClass(className);
            if(DeployUtils.isSpringBeanClass(clazz)) {
                DefaultListableBeanFactory defaultListableBeanFactory = new DefaultListableBeanFactory();
                defaultListableBeanFactory.removeBeanDefinition(DeployUtils.transformName(className));
            }
        }
    }
}
