package priv.hb.sample.api;

import com.aliyun.dataworks_public20200518.Client;
import com.aliyun.tea.*;
import com.aliyun.dataworks_public20200518.models.*;
import com.aliyun.teaopenapi.models.*;
import com.aliyun.teautil.models.*;

import java.util.List;
import java.util.Map;

;


/**
 * @author hubin
 * @date 2022年08月22日 11:11
 */
public class CreateTable {

    public static boolean createTable(List<CreateTableRequest.CreateTableRequestColumns> cloumns, Map<String, Object> map, Client client) throws Exception {
        CreateTableRequest createTableRequest = CreateTableRequest.build(map);
        createTableRequest.setColumns(cloumns);
        RuntimeOptions runtime = new RuntimeOptions();

        try {
            // 复制代码运行请自行打印 API 的返回值
            CreateTableResponse tableWithOptions = client.createTableWithOptions(createTableRequest, runtime);
            return tableWithOptions.getBody().getTaskInfo().getStatus().toUpperCase().equals("SUCCESS");
        } catch (TeaException error) {
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        }

        return false;


    }


    public static com.aliyun.dataworks_public20200518.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dataworks.cn-hangzhou.aliyuncs.com";
        return new com.aliyun.dataworks_public20200518.Client(config);
    }

    public static void main(String[] args_) throws Exception {
        java.util.List<String> args = java.util.Arrays.asList(args_);
        com.aliyun.dataworks_public20200518.Client client = CreateTable.createClient("accessKeyId", "accessKeySecret");
        CreateTableRequest.CreateTableRequestColumns columns0 = new CreateTableRequest.CreateTableRequestColumns()
                .setColumnName("id")
                .setColumnType("STRING");
        CreateTableRequest.CreateTableRequestColumns columns1 = new CreateTableRequest.CreateTableRequestColumns()
                .setColumnName("dayid")
                .setColumnType("STRING")
                .setIsPartitionCol(true);
        CreateTableRequest.CreateTableRequestColumns columns2 = new CreateTableRequest.CreateTableRequestColumns()
                .setColumnName("hour")
                .setColumnType("INT")
                .setIsPartitionCol(true);
        CreateTableRequest createTableRequest = new CreateTableRequest()
                .setColumns(java.util.Arrays.asList(
                        columns0,
                        columns1,
                        columns2
                ))
                .setTableName("test_table_hb_1")
                .setProjectId(48843L)
                .setAppGuid("odps.ybtest_ytdw_default");
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            client.createTableWithOptions(createTableRequest, runtime);
        } catch (TeaException error) {
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
    }

}