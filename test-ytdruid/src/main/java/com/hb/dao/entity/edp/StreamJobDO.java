package com.hb.dao.entity.edp;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author hubin
 * @date 2022年04月28日 10:46 上午
 */
@Table(name = "stream_job")
public class StreamJobDO {
    @Id
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "StreamJobDO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
