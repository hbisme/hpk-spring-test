package priv.hb.sample.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * @author hubin
 * @date 2022年03月08日 10:37 上午
 */
@TableName("t_redpill_tag")
public class TRedpillTagDO {
    @TableId(type = IdType.AUTO)
    public Long id;
    public String name;
    public Integer count;
    public Integer type;
    private Date createTime;

    @TableLogic
    @TableField("is_deleted")
    public Integer isDeleted;

    public TRedpillTagDO() {
    }

    public TRedpillTagDO(String name, Integer count, Integer type, Date createTime, Integer is_delete) {
        this.name = name;
        this.count = count;
        this.type = type;
        this.createTime = createTime;
        this.isDeleted = is_delete;
    }

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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "TRedpillTagDO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", count=" + count +
                ", type=" + type +
                ", createTime=" + createTime +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
