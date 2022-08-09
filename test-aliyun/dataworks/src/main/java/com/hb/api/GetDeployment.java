package com.hb.api;

import com.aliyun.tea.*;
import com.aliyun.dataworks_public20200518.models.*;
import com.aliyun.teaopenapi.models.*;
import com.aliyun.teautil.models.*;

/**
 * @author hubin
 * @date 2022年08月09日 14:12
 */
public class GetDeployment {



    public static void main(String[] args_) throws Exception {
        java.util.List<String> args = java.util.Arrays.asList(args_);
        com.aliyun.dataworks_public20200518.Client client = Common.createClient("LTAI5tPqJz5fnRjTZnfQGoVy", "DwWFusk7ChskWDmxpfROBkdUPJ6Cth");
        GetDeploymentRequest getDeploymentRequest = new GetDeploymentRequest()
                .setDeploymentId(12273913L)
                .setProjectId(48843L);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            client.getDeploymentWithOptions(getDeploymentRequest, runtime);
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
