package com.hb.dao.mapper.hirac;

import com.hb.dao.entity.hirac.HiracJobDO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HiracJobMapper {
    HiracJobDO selectById(@Param("jobId") Long jobId);
}
