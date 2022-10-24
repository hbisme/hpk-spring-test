package priv.hb.sample.service;

import priv.hb.sample.dao.entity.hirac.HiracActionDO;
import priv.hb.sample.dao.entity.hirac.HiracJobDO;

/**
 * @author hubin
 * @date 2022年04月27日 3:31 下午
 */
public interface HiracJobService {

    HiracJobDO selectById(Long id);

    HiracActionDO selectActionById(Long id);
}
