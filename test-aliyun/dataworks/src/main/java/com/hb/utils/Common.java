package com.hb.utils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.teaopenapi.models.Config;
import com.hb.api.ListFiles;
import com.hb.argument.ListFilesInputArgs;
import com.hb.argument.ListFilesOutputArgs;
import com.hb.config.CrmComputerJobConfig;
import com.hb.config.ComputerJobConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.java.Log;

/**
 * @author hubin
 * @date 2022年08月09日 14:32
 */
@Log
public class Common {
    /**
     * 使用AK&SK初始化账号Client
     *
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

    public static com.aliyun.dataworks_public20200518.Client createClient() throws Exception {
        Config config = new Config()
                // 您的 AccessKey ID
                .setAccessKeyId(ComputerJobConfig.ACCESS_KEY_ID)
                // 您的 AccessKey Secret
                .setAccessKeySecret(ComputerJobConfig.ACCESS_KEY_SECRET);
        // 访问的域名
        config.endpoint = "dataworks.cn-hangzhou.aliyuncs.com";
        return new com.aliyun.dataworks_public20200518.Client(config);
    }

    public static com.aliyun.dataworks_public20200518.Client createCrmClient() throws Exception {
        CrmComputerJobConfig crmConfig = new CrmComputerJobConfig();
        Config aliConfig = new Config()
                // 您的 AccessKey ID
                .setAccessKeyId(crmConfig.getAccessKeyId())
                // 您的 AccessKey Secret
                .setAccessKeySecret(crmConfig.getAccessKeySecret());
        // 访问的域名
        aliConfig.endpoint = crmConfig.getEndpoint();
        return new com.aliyun.dataworks_public20200518.Client(aliConfig);
    }

    public static Long getFileIdByName(String name, com.aliyun.dataworks_public20200518.Client client, Long projectId, String fileFolderPath) throws Exception {
        ListFilesInputArgs listFilesInputArgs = new ListFilesInputArgs(projectId);
        listFilesInputArgs.setKeyword(name);
        listFilesInputArgs.setFileFolderPath(fileFolderPath);

        Map<String, Object> map = Common.toMap(listFilesInputArgs);
        List<ListFilesOutputArgs> listFilesOutputArgs = ListFiles.listFiles(map, client);
        List<ListFilesOutputArgs> collect = listFilesOutputArgs.stream().filter(x -> x.getFileName().equals(name)).collect(Collectors.toList());
        if (collect.size() == 1) {
            return collect.get(0).getFileId();
        }
        log.warning("通过名称: " + name + "获取文件id失败.");
        return -1L;
    }

    public static Map<String, Object> toMap(Object object) {
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(object);
        Map map = jsonObject.toJavaObject(Map.class);
        return map;
    }

    public static void main(String[] args) {
        List<String> dependNames = new ArrayList<String>();
        dependNames.add("a");
        dependNames.add("b");


        // todo 测试加的空间名前缀,后面删除
        dependNames = io.vavr.collection.List.ofAll(dependNames).map(x -> "ybtest_ytdw_default." + x).toJavaList();

        String inputList = io.vavr.collection.List.ofAll(dependNames).mkString(",");
        System.out.println(inputList);


    }
}
