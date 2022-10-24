package priv.hb.sample.service;

import priv.hb.sample.dao.entity.StreamTaskDO;

import java.util.List;

public interface StreamTaskQueryService {
    StreamTaskDO selectByJobId(Long jobId);

    StreamTaskDO selectByPrimaryKey(Long id);

    List<StreamTaskDO> selectByExample(Long streamJobId, String name);
}
