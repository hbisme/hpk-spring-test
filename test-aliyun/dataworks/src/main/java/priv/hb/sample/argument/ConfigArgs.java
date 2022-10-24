package priv.hb.sample.argument;

import lombok.Data;

/**
 * 离线SQL调度任务的公共配置
 * @author hubin
 * @date 2022年08月09日 14:34
 */
@Data
public class ConfigArgs {
    private Long projectId;
    private String accessKeyId;
    private String accessKeySecret;
    // 资源组
    private String resourceGroupIdentifier;
}
