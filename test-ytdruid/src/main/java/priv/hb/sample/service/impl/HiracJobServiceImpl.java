package priv.hb.sample.service.impl;

import priv.hb.sample.dao.entity.hirac.HiracActionDO;
import priv.hb.sample.dao.entity.hirac.HiracJobDO;
import priv.hb.sample.dao.mapper.hirac.HiracActionMapper;
import priv.hb.sample.dao.mapper.hirac.HiracJobMapper;
import priv.hb.sample.service.HiracJobService;

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
