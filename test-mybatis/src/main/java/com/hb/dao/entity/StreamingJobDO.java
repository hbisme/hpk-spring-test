package com.hb.dao.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class StreamingJobDO implements Serializable {

    private static final long serialVersionUID = -1;

    /**
     * PK
     */
    private Long id;

    /**
     * job名称
     */
    private String name;

    /**
     * 任务描述
     */
    private String jobDesc;

    /**
     * 任务类型
     */
    private Integer type;

    /**
     * 任务ownerId
     */
    private String ownerUid;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date editTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 编辑人
     */
    private String editor;

    /**
     * 是否删除
     */
    private Integer isDeleted;

    public StreamingJobDO() {
    }

    public StreamingJobDO(Long id, Integer isDeleted) {
        this.id = id;
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "StreamingJobDO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", jobDesc='" + jobDesc + '\'' +
                ", type=" + type +
                ", ownerUid='" + ownerUid + '\'' +
                ", createTime=" + createTime +
                ", editTime=" + editTime +
                ", creator='" + creator + '\'' +
                ", editor='" + editor + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
