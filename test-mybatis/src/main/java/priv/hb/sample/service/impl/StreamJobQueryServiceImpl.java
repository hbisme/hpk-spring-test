package priv.hb.sample.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import priv.hb.sample.dao.mappers.StreamJobMapper;
import priv.hb.sample.dao.entity.StreamingJobDO;
import priv.hb.sample.dao.query.StreamJobDalQuery;
import priv.hb.sample.service.StreamJobQueryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StreamJobQueryServiceImpl implements StreamJobQueryService {

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
