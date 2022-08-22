package com.hb.api;

import com.aliyun.dataworks_public20200518.Client;
import com.aliyun.tea.*;
import com.aliyun.dataworks_public20200518.models.*;
import com.aliyun.teaopenapi.models.*;
import com.aliyun.teautil.models.*;

import java.util.List;
import java.util.Map;

import lombok.extern.java.Log;

/**
 * @author hubin
 * @date 2022年08月17日 19:02
 */
@Log
public class ListDeployments {
    public static List<ListDeploymentsResponseBody.ListDeploymentsResponseBodyDataDeployments> listDeployments(Map<String, Object> map, Client client) throws Exception {
        ListDeploymentsRequest listDeploymentsRequest = ListDeploymentsRequest.build(map);
        RuntimeOptions runtime = new RuntimeOptions();

        try {
            // 复制代码运行请自行打印 API 的返回值
            ListDeploymentsResponse listDeploymentsResponse = client.listDeploymentsWithOptions(listDeploymentsRequest, runtime);
            return listDeploymentsResponse.getBody().getData().getDeployments();

        } catch (TeaException error) {
            log.warning(error.message);

        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            log.warning(error.message);
        }

        return null;
    }


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
        com.aliyun.dataworks_public20200518.Client client = ListDeployments.createClient("accessKeyId", "accessKeySecret");
        ListDeploymentsRequest listDeploymentsRequest = new ListDeploymentsRequest()
                .setProjectId(48843L);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            client.listDeploymentsWithOptions(listDeploymentsRequest, runtime);
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
