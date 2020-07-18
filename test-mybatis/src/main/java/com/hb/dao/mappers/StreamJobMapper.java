package com.hb.dao.mappers;

import com.hb.domain.streaming.entity.StreamingJobDO;
import com.hb.domain.streaming.query.StreamJobDalQuery;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StreamJobMapper {

    /**
     * 主键查询
     * @param id
     * @return
     */
    StreamingJobDO selectByPrimaryKey(Long id);

    /**
     * 多条件查询作业信息
     * @param
     * @return
     */
    List<StreamingJobDO> selectByJobParam(StreamJobDalQuery streamJobDalQuery);


}
