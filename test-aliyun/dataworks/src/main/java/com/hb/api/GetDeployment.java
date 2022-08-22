package com.hb.api;

import com.aliyun.tea.*;
import com.aliyun.dataworks_public20200518.models.*;
import com.aliyun.dataworks_public20200518.Client;
import com.aliyun.teautil.models.*;
import com.hb.utils.Common;

/**
 * @author hubin
 * @date 2022年08月09日 14:12
 */
public class GetDeployment {
    public static boolean waitDeployment(Long projectId, Long deploymentId, Client client) {
        long before = System.currentTimeMillis();
        GetDeploymentRequest getDeploymentRequest = new GetDeploymentRequest()
                .setDeploymentId(deploymentId)
                .setProjectId(projectId);
        RuntimeOptions runtime = new RuntimeOptions();
        GetDeploymentResponseBody.GetDeploymentResponseBodyDataDeployment deployment;
        Integer checkingStatus;
        Integer status;

        try {
            // 复制代码运行请自行打印 API 的返回值
            do {
                deployment = client.getDeploymentWithOptions(getDeploymentRequest, runtime).getBody().getData().getDeployment();
                checkingStatus = deployment.getCheckingStatus();
                status = deployment.getStatus();
                Thread.sleep(1000);
                long after = System.currentTimeMillis();
                if (after - before > 5000) {
                    return false;
                }

            }
            // 判断是否已经Deployment操作完成
            while (checkingStatus != null || status != 1);
            return true;
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

    public static void main(String[] args_) throws Exception {
        java.util.List<String> args = java.util.Arrays.asList(args_);
        com.aliyun.dataworks_public20200518.Client client = Common.createClient();
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
