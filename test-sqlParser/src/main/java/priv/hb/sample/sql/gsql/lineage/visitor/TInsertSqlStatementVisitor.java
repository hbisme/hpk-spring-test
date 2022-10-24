package priv.hb.sample.sql.gsql.lineage.visitor;

import java.util.List;

import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import io.vavr.Tuple2;
import lombok.Getter;
import priv.hb.sample.sql.gsql.utils.Common;

/**
 * @author hubin
 * @date 2022年10月10日 11:25
 */
public class TInsertSqlStatementVisitor extends TParseTreeVisitor {

    @Getter
    String targetTableName;

    /**
     * 静态分区信息
     */
    @Getter
    List<Tuple2<String, String>> staticPartitions;

    // 动态分区信息
    @Getter
    List<String> dynamicPartitions;

    @Override
    public void preVisit(TInsertSqlStatement stmt) {
        super.preVisit(stmt);

        targetTableName = Common.getTargetTableNameFunc(stmt);

        staticPartitions = Common.getStaticPartition(stmt);
        dynamicPartitions = Common.getDynamicPartition(stmt);


    }


}
