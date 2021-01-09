package com.hb.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hb.dao.mappers.StreamJobMapper;
import com.hb.domain.streaming.entity.StreamingJobDO;
import com.hb.domain.streaming.query.StreamJobDalQuery;
import com.hb.service.StreamQueryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StreamQueryServiceImpl implements StreamQueryService {

    @Autowired
    StreamJobMapper streamJobMapper;

    @Override
    public StreamingJobDO selectByPrimaryKey(Long id) {
        StreamingJobDO res = streamJobMapper.selectByPrimaryKey(60L);
        return res;
    }

    @Override
    public List<StreamingJobDO> selectByJobParam(StreamJobDalQuery streamJobDalQuery) {
        List<StreamingJobDO> res = streamJobMapper.selectByJobParam(streamJobDalQuery);
        return res;
    }

    @Override
    public Page<StreamingJobDO> pageSelectByJobParam(StreamJobDalQuery streamJobDalQuery) {
        PageHelper.startPage(1, 2, true);
        Page<StreamingJobDO> page = streamJobMapper.pageSelectByJobParam(streamJobDalQuery);
        return page;
    }
}
