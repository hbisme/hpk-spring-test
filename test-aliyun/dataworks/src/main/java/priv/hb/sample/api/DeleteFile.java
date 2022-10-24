package priv.hb.sample.api;


import com.aliyun.tea.*;
import com.aliyun.dataworks_public20200518.models.*;
import com.aliyun.dataworks_public20200518.Client;
import com.aliyun.teautil.models.*;
import priv.hb.sample.utils.Common;

import java.util.Map;

import lombok.extern.java.Log;


/**
 * @author hubin
 * @date 2022年08月10日 11:30
 */
@Log
public class DeleteFile {
    public static boolean deleteFile(Map<String, Object> map, Client client) throws Exception {
        DeleteFileRequest deleteFileRequest = DeleteFileRequest.build(map);
        RuntimeOptions runtime = new RuntimeOptions();

        try {
            // 复制代码运行请自行打印 API 的返回值
            DeleteFileResponse deleteFileResponse = client.deleteFileWithOptions(deleteFileRequest, runtime);
            return deleteFileResponse.getBody().getSuccess();
        } catch (TeaException error) {
            log.warning("删除文件失败1,错误信息: " +  error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            log.warning("删除文件失败2,错误信息: " +  error.message);
        }

        return false;
    }




    public static void main(String[] args_) throws Exception {
        java.util.List<String> args = java.util.Arrays.asList(args_);
        com.aliyun.dataworks_public20200518.Client client = Common.createClient("LTAI5tPqJz5fnRjTZnfQGoVy", "DwWFusk7ChskWDmxpfROBkdUPJ6Cth");
        DeleteFileRequest deleteFileRequest = new DeleteFileRequest()
                .setFileId(505961472L)
                .setProjectId(48843L);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            client.deleteFileWithOptions(deleteFileRequest, runtime);
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
