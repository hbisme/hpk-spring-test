package com.hb.argument;

import lombok.Builder;
import lombok.Data;

/**
 * @author hubin
 * @date 2022年08月10日 11:41
 */
@Data
public class ListFilesInputArgs {
    private Long projectId;

    public ListFilesInputArgs(Long projectId) {
        this.projectId = projectId;
    }

    private String Keyword;

    private String owner;

    private String fileFolderPath;

    private String useType;

    private String fileTypes;



}
