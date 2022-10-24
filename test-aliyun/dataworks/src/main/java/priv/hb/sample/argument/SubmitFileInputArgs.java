package priv.hb.sample.argument;

import lombok.Data;

/**
 * @author hubin
 * @date 2022年08月09日 14:21
 */
@Data
public class SubmitFileInputArgs {
    private Long fileId;
    private Long projectId;
    private Boolean skipAllDeployFileExtensions = true;

    public SubmitFileInputArgs(Long fileId, Long projectId) {
        this.fileId = fileId;
        this.projectId = projectId;
    }
}
