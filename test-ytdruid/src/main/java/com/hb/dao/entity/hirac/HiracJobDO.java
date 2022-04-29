package com.hb.dao.entity.hirac;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static javax.persistence.GenerationType.TABLE;

/**
 * @author hubin
 * @date 2022年04月27日 3:04 下午
 */
@Table(name = "hera_job")
public class HiracJobDO {
    @Id
    @GeneratedValue(strategy = TABLE)
    private Long id;


    private String name;

    private String groupId;

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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "HiracJobDO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", groupId='" + groupId + '\'' +
                '}';
    }
}
