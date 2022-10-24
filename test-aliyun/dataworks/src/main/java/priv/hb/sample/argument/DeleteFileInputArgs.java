package priv.hb.sample.argument;

import lombok.Data;

/**
 * @author hubin
 * @date 2022年08月10日 11:33
 */
@Data
public class DeleteFileInputArgs {
    private Long fileId;
    private Long projectId;

    public DeleteFileInputArgs(Long fileId, Long projectId) {
        this.fileId = fileId;
        this.projectId = projectId;
    }
}
