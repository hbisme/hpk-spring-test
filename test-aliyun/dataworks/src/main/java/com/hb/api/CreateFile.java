package com.hb.api;

import com.aliyun.tea.*;
import com.aliyun.dataworks_public20200518.models.*;
import com.aliyun.teautil.models.*;
import com.hb.utils.Common;

import java.util.Map;

import lombok.extern.java.Log;

/**
 * @author hubin
 * @date 2022年08月09日 13:56
 */
@Log
public class CreateFile {
    public static Long createFile(Map<String, Object> map) throws Exception {
        com.aliyun.dataworks_public20200518.Client client = Common.createClient();
        CreateFileRequest createFileRequest = CreateFileRequest.build(map);

        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            CreateFileResponse fileWithOptions = client.createFileWithOptions(createFileRequest, runtime);
            if (fileWithOptions.statusCode == 200) {

                Long fileId = fileWithOptions.getBody().getData();
                return fileId;
            }

        } catch (TeaException error) {
            log.warning(error.message);

        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            log.warning(error.message);
        }

        return -1L;
    }



    // public static Long createFile(CreateFileInputArgs createFileInputArgs) throws Exception {
    //     com.aliyun.dataworks_public20200518.Client client = Common.createClient("LTAI5tPqJz5fnRjTZnfQGoVy", "DwWFusk7ChskWDmxpfROBkdUPJ6Cth");
    //     CreateFileRequest createFileRequest = new CreateFileRequest()
    //             .setFileFolderPath(createFileInputArgs.getFileFolderPath())
    //             .setProjectId(createFileInputArgs.getProjectId())
    //             .setFileName(createFileInputArgs.getFileName())
    //             .setFileType(createFileInputArgs.getFileType())
    //             .setContent(createFileInputArgs.getContent())
    //             .setInputList(createFileInputArgs.getInputList());
    //
    //
    //
    //
    //     RuntimeOptions runtime = new RuntimeOptions();
    //     try {
    //         // 复制代码运行请自行打印 API 的返回值
    //         CreateFileResponse fileWithOptions = client.createFileWithOptions(createFileRequest, runtime);
    //         if (fileWithOptions.statusCode == 200) {
    //
    //             Long fileId = fileWithOptions.getBody().getData();
    //             return fileId;
    //         }
    //
    //     } catch (TeaException error) {
    //         log.warning(error.message);
    //
    //     } catch (Exception _error) {
    //         TeaException error = new TeaException(_error.getMessage(), _error);
    //         log.warning(error.message);
    //     }
    //
    //     return -1L;
    // }


    public static void main(String[] args_) throws Exception {
        java.util.List<String> args = java.util.Arrays.asList(args_);
        com.aliyun.dataworks_public20200518.Client client = Common.createClient("LTAI5tPqJz5fnRjTZnfQGoVy", "DwWFusk7ChskWDmxpfROBkdUPJ6Cth");
        CreateFileRequest createFileRequest = new CreateFileRequest()
                .setFileFolderPath("业务流程/hb/MaxCompute")
                .setProjectId(48843L)
                .setFileName("test_file3")
                .setFileDescription("文件描述2")
                .setFileType(10)
                .setContent("select 232")
                .setInputList("ybtest_ytdw_default_root");
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            CreateFileResponse fileWithOptions = client.createFileWithOptions(createFileRequest, runtime);
            if (fileWithOptions.statusCode == 200) {

                Long fileId = fileWithOptions.getBody().getData();
                System.out.println(fileId);
            }

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
