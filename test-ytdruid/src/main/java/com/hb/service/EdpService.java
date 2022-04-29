package com.hb.service;

import com.hb.dao.entity.edp.StreamJobDO;

/**
 * @author hubin
 * @date 2022年04月28日 2:00 下午
 */
public interface EdpService {
    StreamJobDO selectByid(Long id);
}
