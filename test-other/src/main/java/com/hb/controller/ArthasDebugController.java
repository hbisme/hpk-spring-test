package com.hb.controller;

import com.hb.service.ArthasDebug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArthasDebugController {
    @Autowired
    private ArthasDebug arthasDebug;


    @GetMapping("asTest1")
    public String asTest1(@RequestParam String input) {
        return arthasDebug.test1(input);
    }

}
