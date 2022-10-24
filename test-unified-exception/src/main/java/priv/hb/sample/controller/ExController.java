package priv.hb.sample.controller;

import priv.hb.sample.asserts.ArgumentAssert;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExController {

    @GetMapping("/test1")
    public String test1() {
        return "ok";
    }

    @GetMapping("/test2")
    public String test2(@RequestParam String p) {
        ArgumentAssert.notNumber(p);
        return p;
    }
}
