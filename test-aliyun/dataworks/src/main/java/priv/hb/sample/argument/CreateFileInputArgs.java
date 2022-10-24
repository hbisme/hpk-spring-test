package priv.hb.sample.argument;

import lombok.Data;

/**
 * @author hubin
 * @date 2022年08月09日 14:13
 */
@Data
public class CreateFileInputArgs {
    private String fileName;
    private String fileFolderPath;
    private Long projectId;
    private Integer fileType = 10;
    private String inputList;
    private String content;

    public CreateFileInputArgs(String fileName, String fileFolderPath, Long projectId, String inputList, String content) {
        this.fileName = fileName;
        this.fileFolderPath = fileFolderPath;
        this.projectId = projectId;
        this.inputList = inputList;
        this.content = content;
    }

    // 以下的参数都是可选的
    private String cronExpress;

    private Integer autoRerunTimes = 1;

    private String resourceGroupIdentifier;

    // 文件所有者的用户ID
    private String owner;

    // public Map<String, Object> toMap() {
    //     JSONObject jsonObject = (JSONObject) JSONObject.toJSON(this);
    //     Map map = jsonObject.toJavaObject(Map.class);
    //     return map;
    // }



}
