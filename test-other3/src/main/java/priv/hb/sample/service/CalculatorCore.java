package priv.hb.sample.service;

import org.springframework.stereotype.Service;

/**
 * @author hubin
 * @date 2022年06月13日 10:46
 */
@Service
public class CalculatorCore {
    public int add(int a, int b) {
        return a + b;
    }
}
