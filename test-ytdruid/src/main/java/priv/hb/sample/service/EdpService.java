package priv.hb.sample.service;

import priv.hb.sample.dao.entity.edp.StreamJobDO;

/**
 * @author hubin
 * @date 2022年04月28日 2:00 下午
 */
public interface EdpService {
    StreamJobDO selectByid(Long id);
}
