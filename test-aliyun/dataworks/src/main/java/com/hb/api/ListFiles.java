package com.hb.api;

import com.aliyun.tea.*;
import com.aliyun.dataworks_public20200518.models.*;
import com.aliyun.teautil.models.*;
import com.hb.argument.ListFilesOutputArgs;
import com.hb.utils.Common;
import com.hb.utils.Mapper;

import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hubin
 * @date 2022年08月10日 11:45
 */
@Log
// @Slf4j
public class ListFiles {

    public static List<ListFilesOutputArgs> listFiles(Map<String, Object> map) throws Exception {
        com.aliyun.dataworks_public20200518.Client client = Common.createClient();
        ListFilesRequest listFilesRequest = ListFilesRequest.build(map);

        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            ListFilesResponse listFilesResponse = client.listFilesWithOptions(listFilesRequest, runtime);
            List<ListFilesResponseBody.ListFilesResponseBodyDataFiles> files = listFilesResponse.getBody().getData().getFiles();
            Mapper mapper = Mappers.getMapper(Mapper.class);
            List<ListFilesOutputArgs> listFilesOutputArgs = io.vavr.collection.List.ofAll(files).map(x -> {
                return mapper.responseToOutput(x);
            }).toJavaList();
            return listFilesOutputArgs;

        } catch (TeaException error) {
            log.warning(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            log.warning(error.message);
        }
        return null;

    }



    public static void main(String[] args_) throws Exception {
        java.util.List<String> args = java.util.Arrays.asList(args_);
        com.aliyun.dataworks_public20200518.Client client = Common.createClient("accessKeyId", "accessKeySecret");
        ListFilesRequest listFilesRequest = new ListFilesRequest()
                .setProjectId(48843L)
                .setKeyword("");
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            client.listFilesWithOptions(listFilesRequest, runtime);
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
