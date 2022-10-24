package priv.hb.sample.dao.entity.hirac;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author hubin
 * @date 2022年04月27日 4:02 下午
 */
@Table(name = "hera_action")
public class HiracActionDO {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    private String cronExpression;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "HiracActionDO{" +
                "id=" + id +
                ", cronExpression='" + cronExpression + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
