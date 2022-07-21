package com.hb.service.impl;

import com.hb.service.Calculator;
import com.hb.service.CalculatorCore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hubin
 * @date 2022年06月13日 10:44
 */
@Service
public class Calculator2Impl implements Calculator {

    @Autowired
    CalculatorCore calculatorCore;

    /**
     * 注解方式
     * @param a
     * @param b
     * @return
     */
    @Override
    public int calculate(int a, int b) {
        int c = calculatorCore.add(a,b);
        return c;
    }

    /**
     * 反射方式
     * @param a
     * @param b
     * @return
     */
    @Override
    public int add(int a, int b) {
        return new CalculatorCore().add(a, b);

    }
}
