package priv.hb.sample.tool.vavr.monad;

import lombok.Data;

/**
 * @author hubin
 * @date 2022年10月28日 15:54
 */
@Data
public class Environment {
    private String prefix = "$$";
    private Integer base = 100;

    public Environment() {
    }

    public Environment(String prefix, Integer base) {
        this.prefix = prefix;
        this.base = base;
    }
}
