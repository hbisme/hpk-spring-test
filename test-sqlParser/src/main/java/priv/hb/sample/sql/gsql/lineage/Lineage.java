package priv.hb.sample.sql.gsql.lineage;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author hubin
 * @date 2022年09月30日 09:40
 */
@Data
@AllArgsConstructor
public class Lineage {
    private String id;

    /**
     * 通过什么命令 hive, spark-sql, sqoop-import, sqoop-export 等
     */
    private String byCmd;

    /**
     * 命令在脚本中执行的时间戳，毫秒级，可以用来确定命令的先后执行顺序
     */
    private Long launchTime;

    private LineageTarget target;

    private List<LineageSource> sources;

    public static Lineage relationship(LineageTarget target, List<LineageSource> sources) {
        return new Lineage(null, null, null, target, sources);
    }
}
