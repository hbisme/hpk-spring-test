package priv.hb.sample.dbunit;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * DBUnit基类
 */
public class DBUnitBase {
    protected Connection conn = null;

    protected IDatabaseConnection connection = null;

    /**
     * 备份数据目录
     */
    protected String backupDataPath = "src/test/resources/dbunit/backup/";

    /**
     * 测试数据文件目录
     */
    protected String testDataPath = "src/test/resources/dbunit/test/";

    /**
     * 数据备份文件
     */
    protected File file = null;

    /**
     * 所有备份文件
     */
    protected List<File> files = null;

    /**
     * 获取数据库连接
     *
     * @throws Exception
     * @returnjava.sql.Connection
     */
    protected Connection getConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        // 连接DB
        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://rdsxt5l78bid42x9ddylc832.mysql.rds.aliyuncs.com:3306/stream_test", "yangtuojia001", "yangtuojia001");
        return conn;
    }

    /**
     * 通过表名备份数据
     *
     * @param tableName      表名
     * @param backupFileName 备份文件名
     * @throws Exception
     */
    protected void backupData(String tableName, String backupFileName)
            throws Exception {
        try {
            conn = getConnection();
            connection = new DatabaseConnection(conn);
            if (null != tableName && !"".equals(tableName)) {
                // 通过表名备份单张表单数据
                QueryDataSet backupDataSet = new QueryDataSet(connection);
                backupDataSet.addTable(tableName);
                // 设置备份文件路径
                file = new File(backupDataPath + backupFileName);
                FlatXmlDataSet.write(backupDataSet, new FileWriter(file),
                        "UTF-8");
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCon();
        }
    }


    /**
     * 通过xml文件恢复数据
     *
     * @param file 路径+文件名
     */
    @SuppressWarnings("deprecation")
    public void recoverData(File file) {
        try {
            conn = getConnection();
            connection = new DatabaseConnection(conn);
            FlatXmlDataSet dataSet = new FlatXmlDataSet(file);
            DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCon();
        }
    }

    /**
     * 关闭连接
     */
    protected void closeCon() {
        try {
            if (connection != null) {
                connection.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
