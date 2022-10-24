package priv.hb.sample.argument;

import lombok.Data;

/**
 * @author hubin
 * @date 2022年08月10日 11:46
 */
@Data
public class ListFilesOutputArgs {
    private Long fileId;

    private String fileName;

    private String createUser;

    private String fileDescription;

    private String owner;

    private String useType;

    private String fileType;

    private String lastEditUser;

}
