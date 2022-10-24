package priv.hb.sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hubin
 * @date 2022年06月13日 10:21
 */
@RestController
@Slf4j
public class Controller1 {

    @GetMapping("/atest1")
    public String test1() {
        log.info("okkkk");
        return "ok";
    }
}
