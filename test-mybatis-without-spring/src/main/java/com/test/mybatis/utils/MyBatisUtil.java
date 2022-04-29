package com.test.mybatis.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author hubin
 * @date 2022年03月08日 11:39 上午
 */
public class MyBatisUtil {
    private static final String configFile = "mybatis-config.xml";





    public static SqlSession getSession() {
        SqlSession sqlSession = null;

        try {
            InputStream inputStream = Resources.getResourceAsStream(configFile);
            // 1、获取sqlSessionFactory对象
            SqlSessionFactory build = new SqlSessionFactoryBuilder().build(inputStream);
            // 2、获取sqlSession对象
            sqlSession = build.openSession();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return sqlSession;
    }

    public static void closeSession(SqlSession sqlSession) {
        sqlSession.close();
    }

}
