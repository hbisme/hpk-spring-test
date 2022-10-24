package priv.hb.sample.test.mybatis.dao.entity;

/**
 * @author hubin
 * @date 2022年03月08日 10:37 上午
 */
public class TRedpillTag {
    public Long id;
    public String name;
    public Integer count;
    public Integer type;
    public Integer is_deleted;

    public TRedpillTag() {
    }

    public TRedpillTag(Long id, String name, Integer count, Integer type, Integer is_delete) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.type = type;
        this.is_deleted = is_delete;
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

    public Integer getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Integer is_deleted) {
        this.is_deleted = is_deleted;
    }

    @Override
    public String toString() {
        return "tRedpillTag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", count=" + count +
                ", type=" + type +
                ", isDelete=" + is_deleted +
                '}';
    }
}
