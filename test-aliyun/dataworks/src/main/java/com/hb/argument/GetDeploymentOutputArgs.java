package com.hb.argument;

import com.sun.org.apache.xpath.internal.operations.Bool;

import lombok.Data;

/**
 * 只有发布包的Status为1并且CheckingStatus为空时，才可以继续进行文件到生产环境的发布。
 *
 * @author hubin
 * @date 2022年08月09日 14:29
 */
@Data
public class GetDeploymentOutputArgs {
    private Integer status;
    private String checkingStatus;


    public Boolean checkDeploymented() {
        if (status == 1 && "".equals(checkingStatus)) {
            return true;
        } else {
            return false;
        }
    }
}
