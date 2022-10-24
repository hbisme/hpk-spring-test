package priv.hb.sample.service.impl;

import priv.hb.sample.dao.entity.StreamTaskDO;
import priv.hb.sample.dao.mappers.StreamTaskAutoMapper;
import priv.hb.sample.dao.mappers.StreamTaskMapper;
import priv.hb.sample.service.StreamTaskQueryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import tk.mybatis.mapper.entity.Example;

@Service
public class StreamTaskQueryServiceImpl implements StreamTaskQueryService {

    @Autowired
    StreamTaskMapper streamTaskMapper;

    @Autowired
    StreamTaskAutoMapper streamTaskAutoMapper;


    @Override
    public StreamTaskDO selectByJobId(Long jobId) {
        StreamTaskDO res = streamTaskMapper.selectByJobId(jobId);
        return res;
    }

    @Override
    public StreamTaskDO selectByPrimaryKey(Long id) {
        StreamTaskDO res = streamTaskAutoMapper.selectByPrimaryKey(id);
        return res;
    }

    /**
     * 使用example的例子
     * 执行的SQL为: SELECT id,stream_job_id,stream_job_name FROM stream_task WHERE ( ( stream_job_id = ? or stream_job_name <> ? ) )
     * @return
     */
    @Override
    public List<StreamTaskDO> selectByExample(Long streamJobId, String name) {
        Example example = new Example(StreamTaskDO.class);
        Example.Criteria criteria = example.createCriteria();
        // 这里要用java类的字段名,而不是mysql里的字段名
        criteria.andEqualTo("streamJobId", streamJobId);
        criteria.orNotEqualTo("streamJobName", name);
        List<StreamTaskDO> res = streamTaskAutoMapper.selectByExample(example);
        return res;
    }
}
