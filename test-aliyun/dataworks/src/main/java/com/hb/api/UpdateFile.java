package com.hb.api;

import com.aliyun.tea.*;
import com.aliyun.dataworks_public20200518.models.*;
import com.aliyun.dataworks_public20200518.Client;
import com.aliyun.teautil.models.*;
import com.hb.utils.Common;

import java.util.Map;

import lombok.extern.java.Log;

/**
 * @author hubin
 * @date 2022年08月09日 15:01
 */
@Log
public class UpdateFile {
    public static boolean updateFile(Map<String, Object> map, Client client) throws Exception {
        UpdateFileRequest updateFileRequest = UpdateFileRequest.build(map);

        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            UpdateFileResponse updateFileResponse = client.updateFileWithOptions(updateFileRequest, runtime);
            return updateFileResponse.getBody().getSuccess();
        } catch (TeaException error) {
            log.warning("update文件失败1,错误信息: " +  error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            log.warning("update文件失败2,错误信息: " +  error.message);
        }
        return false;
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
