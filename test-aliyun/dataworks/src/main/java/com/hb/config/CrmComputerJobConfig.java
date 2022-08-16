package com.hb.config;

import lombok.Data;

/**
 * CRM配置类
 * @author hubin
 * @date 2022年08月16日 11:01
 */
@Data
public class CrmComputerJobConfig extends JobBseConfig {
    private String accessKeyId = super.getAccessKeyId();
    private String accessKeySecret = super.getAccessKeySecret();

    private Long projectId = 48843L;

    // CRM SQL文件路径
    private String folderPath = "业务流程/hb/MaxCompute";

    private String defaultRoot = "ybtest_ytdw_default_root";

    private String endpoint = super.getEndpoint();
}
