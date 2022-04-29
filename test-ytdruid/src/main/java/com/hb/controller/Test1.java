package com.hb.controller;

import com.hb.dao.entity.hirac.HiracJobDO;
import com.hb.service.HiracJobService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hubin
 * @date 2022年04月27日 3:46 下午
 */
@RestController
public class Test1 {
    @Autowired
    HiracJobService hiracJobService;

    @GetMapping("/test11")
    public String test1() {
        return "test1";
    }

    @GetMapping("/test22")
    public HiracJobDO selectById() {
        return hiracJobService.selectById(10L);
    }

}
