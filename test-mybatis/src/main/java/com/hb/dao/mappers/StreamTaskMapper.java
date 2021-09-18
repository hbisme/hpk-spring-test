package com.hb.dao.mappers;

import com.hb.dao.YtBaseMapper;
import com.hb.dao.entity.StreamTaskDO;

import org.apache.ibatis.annotations.Mapper;

@Mapper
// public interface StreamTaskMapper extends YtBaseMapper<StreamTaskDO> {
public interface StreamTaskMapper {

    public StreamTaskDO selectByJobId(Long id);

}
