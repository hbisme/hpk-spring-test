package priv.hb.sample.service.impl;

import priv.hb.sample.dao.mapper.edp.StreamJobMapper;
import priv.hb.sample.dao.entity.edp.StreamJobDO;
import priv.hb.sample.service.EdpService;

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
