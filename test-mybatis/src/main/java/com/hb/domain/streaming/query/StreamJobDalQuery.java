package com.hb.domain.streaming.query;

import java.io.Serializable;

public class StreamJobDalQuery implements Serializable {
    private static final long serialVersionUID = -1;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 名称
     */
    private String name;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
