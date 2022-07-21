package com.hb.common;

import com.hb.service.Calculator;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import lombok.SneakyThrows;

/**
 * 反射方式热部署
 *
 * @author hubin
 * @date 2022年06月13日 10:52
 */
public class HotDeployWithReflect {
    private static String jarFile = "/Users/hubin/work/ideaProject/myIdeaProjects/hpk-spring-test/test-other3/target/test-other3-1.0-SNAPSHOT.jar";

    /**
     * 反射方式热部署
     *
     * @throws Exception
     */
    public static void hotDeployWithReflect() throws Exception {
        URL url = new URL("file:" + jarFile);
        ClassLoader urlClassLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());//自己定义的classLoader类，把外部路径也加到load路径里，使系统去该路经load对象

        Class<?> clazz = urlClassLoader.loadClass("com.hb.service.impl.CalculatorImpl");
        Calculator calculator = (Calculator) clazz.newInstance();
        int result = calculator.add(1, 22);
        System.out.println(result);

    }



    public static void main(String[] args) throws Exception {
        hotDeployWithReflect();

    }
}
