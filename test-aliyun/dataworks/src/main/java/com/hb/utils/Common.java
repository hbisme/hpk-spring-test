package com.hb.utils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.teaopenapi.models.Config;
import com.hb.config.CrmComputerJobConfig;
import com.hb.config.ComputerJobConfig;

import java.util.Map;

/**
 * @author hubin
 * @date 2022年08月09日 14:32
 */
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
        aliConfig.endpoint =  crmConfig.getEndpoint();
        return new com.aliyun.dataworks_public20200518.Client(aliConfig);
    }

    public static Map<String, Object> toMap(Object object) {
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(object);
        Map map = jsonObject.toJavaObject(Map.class);
        return map;
    }
}
