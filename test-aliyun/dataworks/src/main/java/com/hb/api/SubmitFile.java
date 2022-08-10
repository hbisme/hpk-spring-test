package com.hb.api;

/**
 * @author hubin
 * @date 2022年08月09日 14:09
 */

import com.aliyun.tea.*;
import com.aliyun.dataworks_public20200518.models.*;
import com.aliyun.teautil.models.*;
import com.hb.utils.Common;

import java.util.Map;

import lombok.extern.java.Log;

@Log
public class SubmitFile {
    public static Long submitFile(Map<String, Object> map) throws Exception {
        com.aliyun.dataworks_public20200518.Client client = Common.createClient();
        SubmitFileRequest submitFileRequest = SubmitFileRequest.build(map);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            SubmitFileResponse submitFileResponse = client.submitFileWithOptions(submitFileRequest, runtime);
            return submitFileResponse.getBody().data;

        } catch (TeaException error) {
            log.warning(error.message);

        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            log.warning(error.message);
        }

        return -1L;
    }


    public static void main(String[] args_) throws Exception {
        java.util.List<String> args = java.util.Arrays.asList(args_);
        com.aliyun.dataworks_public20200518.Client client = Common.createClient("LTAI5tPqJz5fnRjTZnfQGoVy", "DwWFusk7ChskWDmxpfROBkdUPJ6Cth");
        SubmitFileRequest submitFileRequest = new SubmitFileRequest()
                .setProjectId(48843L)
                .setFileId(505961472L)
                .setSkipAllDeployFileExtensions(true);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            client.submitFileWithOptions(submitFileRequest, runtime);
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
