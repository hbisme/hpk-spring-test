package com.hb.argument;

import lombok.Data;

/**
 * @author hubin
 * @date 2022年08月09日 15:02
 */
@Data
public class UpdateFileInputArgs {
    private Long fileId;

    private Long projectId;
    // 文件出错后，自动重跑的次数
    private Integer autoRerunTimes=1;

    // 以下的参数都是可选的
    private String content;

    private String cronExpress;

    private String inputList;

    private String resourceGroupIdentifier;

    // 文件所有者的用户ID
    private String owner;







}
