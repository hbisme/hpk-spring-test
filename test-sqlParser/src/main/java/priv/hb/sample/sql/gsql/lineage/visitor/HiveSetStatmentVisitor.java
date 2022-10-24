package priv.hb.sample.sql.gsql.lineage.visitor;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.stmt.hive.THiveSet;
import lombok.Getter;

/**
 * @author hubin
 * @date 2022年10月09日 15:06
 */
@Getter
public class HiveSetStatmentVisitor extends TParseTreeVisitor {
    public static Pattern HiveVarPattern = Pattern.compile("set\\shivevar:([0-9a-zA-Z._\\-]+)[\\s]?=[\\s]?(.+)");


    private Map<String, String> varPool = new HashMap<>();

    @Override
    public void preVisit(THiveSet node) {
        super.preVisit(node);
        TExpression expr = node.getExpr();
        String k = expr.getLeftOperand().toString();
        String value = expr.getRightOperand().toString();
        String key = StringUtils.removeStart(k, "hivevar:");
        varPool.put(key, value);
    }

    @Override
    public String toString() {
        return varPool.toString();
    }

    /**
     * 将sql变量还原为常量
     *
     * @param
     * @return
     */
    public String replaceSql(String sqls) {
        Matcher matcher = HiveVarPattern.matcher(sqls);
        boolean hasVar = matcher.find();


        List<String> splitSqls = Arrays.stream(sqls.split(";")).collect(Collectors.toList());

        String res = io.vavr.collection.List.ofAll(splitSqls).map(sql -> {
            if (hasVar) {
                for (Map.Entry<String, String> var : varPool.entrySet()) {
                    sql = sql.replaceAll("\\$[\\s]*\\{" + "[\\s]*" + "hivevar" + "[\\s]*" + ":" + "[\\s]*" + var.getKey() + "[\\s]*" + "}", var.getValue());

                }
            }
            return sql;
        }).mkString(";\n");
        return res;
    }


}

