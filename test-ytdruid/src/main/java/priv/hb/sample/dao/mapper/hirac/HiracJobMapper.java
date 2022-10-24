package priv.hb.sample.dao.mapper.hirac;

import priv.hb.sample.dao.entity.hirac.HiracJobDO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HiracJobMapper {
    HiracJobDO selectById(@Param("jobId") Long jobId);
}
