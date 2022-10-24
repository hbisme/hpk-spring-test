package priv.hb.sample.api;

import com.aliyun.tea.*;
import com.aliyun.dataworks_public20200518.models.*;
import com.aliyun.dataworks_public20200518.Client;
import com.aliyun.teautil.models.*;
import priv.hb.sample.argument.ListFilesOutputArgs;
import priv.hb.sample.utils.Common;
import priv.hb.sample.utils.Mapper;

import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.extern.java.Log;

/**
 * @author hubin
 * @date 2022年08月10日 11:45
 */
@Log
// @Slf4j
public class ListFiles {

    /**
     * 获取所有目录下的所有文件(会合并分页)
     *
     * @param map
     * @param client
     * @return
     * @throws Exception
     */
    public static List<ListFilesOutputArgs> listFiles(Map<String, Object> map, Client client) throws Exception {
        Integer totalPageNumber = getTotalPageNumber(map, client);
        if (totalPageNumber == -1) {
            return Collections.EMPTY_LIST;
        }

        List<ListFilesOutputArgs> listFilesOutputArgs1 = io.vavr.collection.List.range(1, totalPageNumber + 1).map(x -> {
            try {
                return listFiles(map, client, x);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).flatMap(x -> x).toJavaList();
        return listFilesOutputArgs1;
    }



    public static Integer getTotalPageNumber(Map<String, Object> map, Client client) throws Exception {
        map.put("PageSize", 100);
        ListFilesRequest listFilesRequest = ListFilesRequest.build(map);

        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            ListFilesResponse listFilesResponse = client.listFilesWithOptions(listFilesRequest, runtime);
            Integer count = listFilesResponse.getBody().getData().getTotalCount();
            return count / 100 + 1;

        } catch (TeaException error) {
            log.warning("list文件失败1,错误信息: " + error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            log.warning("list文件失败2,错误信息: " + error.message);
        }
        return -1;

    }

    /**
     * 查询文件夹下有哪些文件, 查询结果有分页.
     *
     * @param map
     * @param client
     * @param pageNumber 第几页,页码从1开始
     * @return
     * @throws Exception
     */
    public static List<ListFilesOutputArgs> listFiles(Map<String, Object> map, Client client, Integer pageNumber) throws Exception {
        map.put("PageSize", 100);
        map.put("PageNumber", pageNumber);
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
            log.warning("list文件失败1,错误信息: " + error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            log.warning("list文件失败2,错误信息: " + error.message);
        }
        return Collections.EMPTY_LIST;

    }




    public static void main(String[] args_) throws Exception {
        List<String> args = java.util.Arrays.asList(args_);
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
