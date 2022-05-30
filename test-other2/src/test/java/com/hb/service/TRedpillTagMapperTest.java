package com.hb.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hb.Application;
import com.hb.dao.entity.TRedpillTagDO;
import com.hb.dao.mapper.TRedpillTagMapper;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 测试mybatisplus.
 * @author hubin
 * @date 2022年03月09日 3:36 下午
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TRedpillTagMapperTest {
    @Autowired
    private TRedpillTagMapper tRedpillTagMapper;


    @Test
    public void testAll() {
        final List<TRedpillTagDO> tRedpillTagDOS = tRedpillTagMapper.selectList(null);
        System.out.println(tRedpillTagDOS);
    }

    /**
     * 测试BaseMapper自带的按主键查询
     */
    @Test
    public void testSelectById() {
        final TRedpillTagDO tRedpillTagDO = tRedpillTagMapper.selectById(1);
        System.out.println(tRedpillTagDO);
    }

    /**
     * 测试SELECT in查询
     */
    @Test
    public void testSelectByIds() {
        final List<TRedpillTagDO> tRedpillTagDOS = tRedpillTagMapper.selectByIds(Arrays.asList(1, 2, 3));
        System.out.println(tRedpillTagDOS);
    }


    /**
     * 测试条件构造器
     */
    @Test
    public void testSelectByTag() {
        List<TRedpillTagDO> tRedpillTagDOs = tRedpillTagMapper.selectByType("0");
        System.out.println(tRedpillTagDOs);
    }

    /**
     * 测试分页查询
     */
    @Test
    public void testSelectPageByCreateTime() {
        Page<TRedpillTagDO> page = new Page<>(1, 10);
        final Date date = new Date(1592878235000L);
        final IPage<TRedpillTagDO> tRedpillTags = tRedpillTagMapper.selectPageByCreateTime(page, date);
        System.out.println(tRedpillTags);
    }

    @Test
    public void insertTest() {
        final TRedpillTagDO hb = new TRedpillTagDO("hb", 1, 1, new Date(), 1);
        tRedpillTagMapper.insert(hb);
    }

    @Test
    public void updateTest() {
        final TRedpillTagDO hb = new TRedpillTagDO("hb2", 2, 2, new Date(), 1);
        hb.setId(20L);
        final int res = tRedpillTagMapper.updateById(hb);
        System.out.println(res);
    }

    /**
     * 这里的删除为逻辑删除
     */
    @Test
    public void deleteTest() {
        tRedpillTagMapper.deleteById(20);
    }

    @Test
    public void getLast() {
        System.out.println(tRedpillTagMapper.getLast());
    }
}
