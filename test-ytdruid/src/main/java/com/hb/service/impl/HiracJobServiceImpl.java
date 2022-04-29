package com.hb.service.impl;

import com.hb.dao.entity.hirac.HiracActionDO;
import com.hb.dao.entity.hirac.HiracJobDO;
import com.hb.dao.mapper.hirac.HiracActionMapper;
import com.hb.dao.mapper.hirac.HiracJobMapper;
import com.hb.service.HiracJobService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hubin
 * @date 2022年04月27日 3:32 下午
 */
@Service
public class HiracJobServiceImpl implements HiracJobService {

    @Autowired
    HiracJobMapper hiracJobMapper;

    @Autowired
    HiracActionMapper hiracActionMapper;

    @Override
    public HiracJobDO selectById(Long id) {
        final HiracJobDO hiracJobDO = hiracJobMapper.selectById(id);
        return hiracJobDO;
    }

    @Override
    public HiracActionDO selectActionById(Long id) {
        final HiracActionDO hiracActionDO = hiracActionMapper.selectByPrimaryKey(id);
        return hiracActionDO;
    }
}
