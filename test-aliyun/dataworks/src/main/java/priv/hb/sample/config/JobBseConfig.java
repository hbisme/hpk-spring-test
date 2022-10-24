package priv.hb.sample.config;

import lombok.Data;

/**
 * CRM,搜索等各业务线的配置基类
 * @author hubin
 * @date 2022年08月16日 14:00
 */
@Data
public abstract class JobBseConfig {
    private String accessKeyId = ComputerJobConfig.ACCESS_KEY_ID;
    private String accessKeySecret = ComputerJobConfig.ACCESS_KEY_SECRET;

    private Long projectId;

    private String folderPath;

    private String defaultRoot;

    private String endpoint = ComputerJobConfig.ENDPOINT;
}
