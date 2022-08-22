package com.hb.api;

import com.aliyun.dataworks_public20200518.Client;
import com.aliyun.dataworks_public20200518.models.CheckMetaTableRequest;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;

import java.util.function.Function;

import io.vavr.control.Try;


/**
 * @author hubin
 * @date 2022年08月22日 13:46
 */
public class CheckMetaTable {

    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public static Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dataworks.cn-hangzhou.aliyuncs.com";
        return new Client(config);
    }

    public static void main(String[] args_) throws Exception {
        java.util.List<String> args = java.util.Arrays.asList(args_);
        Client client = CheckMetaTable.createClient("accessKeyId", "accessKeySecret");
        CheckMetaTableRequest checkMetaTableRequest = new CheckMetaTableRequest()
                .setTableGuid("odps.ybtest_ytdw_default.test_table_hb_1");
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            client.checkMetaTableWithOptions(checkMetaTableRequest, runtime);
        } catch (TeaException error) {
            // 如有需要，请打印 error
            Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            Common.assertAsString(error.message);
        }
    }
}
