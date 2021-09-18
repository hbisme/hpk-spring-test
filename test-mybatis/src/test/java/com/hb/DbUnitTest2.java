package com.hb;

import org.dbunit.DBTestCase;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DbUnitTest2 extends DBTestCase {

    @Resource
    DataSource dataSource;

    IDatabaseConnection iDatabaseConnection;

    @Before
    public void before() throws SQLException, DatabaseUnitException {
        iDatabaseConnection = new DatabaseConnection(dataSource.getConnection());
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return iDatabaseConnection.createDataSet();
    }



}
