package priv.hb.sample.tool.lombok;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * @author hubin
 * @date 2022年07月21日 10:39
 */
@Data
@Accessors(chain = true)
public class UserDemo {
    @NonNull // NonNull 表示构造器要包含这个参数
    private Integer id;
    private String name;
    private int age;
    private String address;

}
