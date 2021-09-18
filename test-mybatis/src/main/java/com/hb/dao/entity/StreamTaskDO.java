package com.hb.dao.entity;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "stream_task")
public class StreamTaskDO implements Serializable {

    @Id
    private Integer id;

    private Integer streamJobId;

    private String streamJobName;

    public StreamTaskDO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStreamJobId() {
        return streamJobId;
    }

    public void setStreamJobId(Integer streamJobId) {
        this.streamJobId = streamJobId;
    }

    public String getStreamJobName() {
        return streamJobName;
    }

    public void setStreamJobName(String streamJobName) {
        this.streamJobName = streamJobName;
    }

    @Override
    public String toString() {
        return "StreamTaskDO{" +
                "id=" + id +
                ", streamJobId=" + streamJobId +
                ", streamJobName='" + streamJobName + '\'' +
                '}';
    }
}
