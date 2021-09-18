package com.hb;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;


public class DbUnitTest {
    @Test
    public void testWriteFile() throws ClassNotFoundException, SQLException, DatabaseUnitException, IOException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection jdbcConnection = DriverManager.getConnection(
                "jdbc:mysql://rdsxt5l78bid42x9ddylc832.mysql.rds.aliyuncs.com:3306/stream_test", "yangtuojia001", "yangtuojia001");

        // 生成DbUnit的数据集的数据库链接
        DatabaseConnection connection = new DatabaseConnection(jdbcConnection);

        // partial database export 部分数据导出
        QueryDataSet partialDataSet = new QueryDataSet(connection);

        partialDataSet.addTable("hb2", "select id from hb2");
        // 会写到模块根目录下
        FlatXmlDataSet.write(partialDataSet, new FileOutputStream("partial.xml"));




        QueryDataSet partialDataSet2 = new QueryDataSet(connection);
        partialDataSet2.addTable("hb2");
        FlatXmlDataSet.write(partialDataSet2, new FileOutputStream("table.xml"));
    }


}
