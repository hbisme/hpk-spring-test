package com.hb.service.impl;

import com.hb.dao.entity.edp.StreamJobDO;
import com.hb.dao.mapper.edp.StreamJobMapper;
import com.hb.service.EdpService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hubin
 * @date 2022年04月28日 2:01 下午
 */
@Service
public class EdpServiceImpl implements EdpService {

    @Autowired
    StreamJobMapper streamJobMapper;

    @Override
    public StreamJobDO selectByid(Long id) {
        return streamJobMapper.selectByPrimaryKey(id);
    }
}
