package com.hb.dbunit;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

import java.io.FileInputStream;
import java.sql.Connection;


public class DBUnitAll extends DBUnitBase {

    @Test
    public void setUpBackup(String fileName) {
        // JDBC数据库连接
        Connection conn = null;
        // DBUnit数据库连接
        IDatabaseConnection connection = null;
        try {
            conn = getConnection();
            // 获得DB连接
            connection = new DatabaseConnection(conn);
            // 备份数据库测试之前的数据
            backupData("hb2", "hb2_bak.xml");
            // 准备数据的读入
            IDataSet dataSet = new FlatXmlDataSet(new FileInputStream(
                    testDataPath + fileName));
            connection.createDataSet(new String[]{});
            DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCon();
        }

    }

    /**
     * 测试之前,将测试数据写入数据库
     */
    public void recoverAllData() {
        super.recoverData(file);
    }

}
