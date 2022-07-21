package com.hb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hubin
 * @date 2022年06月13日 10:21
 */
@RestController
public class Controller1 {

    @GetMapping("/test1")
    public String test1() {
        return "ok";
    }
}
