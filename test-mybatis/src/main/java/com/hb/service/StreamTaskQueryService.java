package com.hb.service;

import com.hb.dao.entity.StreamTaskDO;

import java.util.List;

public interface StreamTaskQueryService {
    StreamTaskDO selectByJobId(Long jobId);

    StreamTaskDO selectByPrimaryKey(Long id);

    List<StreamTaskDO> selectByExample(Long streamJobId, String name);
}
