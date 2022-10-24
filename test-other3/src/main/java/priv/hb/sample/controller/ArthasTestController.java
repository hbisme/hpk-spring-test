package priv.hb.sample.controller;

import priv.hb.sample.service.arthas.ArthasTestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hubin
 * @date 2022年10月20日 13:57
 */
@RestController()
@Slf4j
@RequestMapping("/arthasTest")
public class ArthasTestController {
    @Autowired
    ArthasTestService arthasTestService;


    @GetMapping("/test1")
    public String test1() {
        return arthasTestService.getMyString("arthas");
    }

}
