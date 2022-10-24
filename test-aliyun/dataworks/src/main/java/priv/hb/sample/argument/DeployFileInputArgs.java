package priv.hb.sample.argument;

import lombok.Data;

/**
 * @author hubin
 * @date 2022年08月09日 14:25
 */
@Data
public class DeployFileInputArgs {
    private Long fileId;
    private Long projectId;
    private String comment;

    public DeployFileInputArgs(Long fileId, Long projectId) {
        this.fileId = fileId;
        this.projectId = projectId;
    }
}
