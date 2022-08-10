package com.hb.service;

import com.hb.dto.TestDTO;

import org.springframework.stereotype.Service;

/**
 * @author hubin
 * @date 2022年08月10日 16:04
 */

@Service
public class TestService {

    /**
     * 参数校验实例,实际上应该把参数校验逻辑使用注解来判断
     */
    public Double service(TestDTO testDTO) throws Exception {
        if (testDTO.getNum() <= 0) {
            throw new Exception("输入的数字需要大于0");
        }
        if (testDTO.getType().equals("square")) {
            return Math.pow(testDTO.getNum(), 2);
        }
        if (testDTO.getType().equals("factorial")) {
            double result = 1;
            int num = testDTO.getNum();
            while (num > 1) {
                result = result * num;
                num -= 1;
            }
            return result;
        }
        throw new Exception("未识别的算法");
    }
}
