package com.hb.api;

import com.aliyun.tea.*;
import com.aliyun.dataworks_public20200518.*;
import com.aliyun.dataworks_public20200518.models.*;
import com.aliyun.teaopenapi.*;
import com.aliyun.teaopenapi.models.*;
import com.aliyun.teautil.*;
import com.aliyun.teautil.models.*;

/**
 * @author hubin
 * @date 2022年08月09日 15:01
 */
public class UpdateFile {

    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
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
        com.aliyun.dataworks_public20200518.Client client = Common.createClient("LTAI5tPqJz5fnRjTZnfQGoVy", "DwWFusk7ChskWDmxpfROBkdUPJ6Cth");
        UpdateFileRequest updateFileRequest = new UpdateFileRequest()
                .setAutoRerunTimes(1)
                .setProjectId(48843L)
                .setFileId(505961472L)
                .setContent("select 123");
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            client.updateFileWithOptions(updateFileRequest, runtime);
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
