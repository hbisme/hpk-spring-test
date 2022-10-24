package priv.hb.sample.dao.mappers;

import priv.hb.sample.dao.entity.StreamTaskDO;

import org.apache.ibatis.annotations.Mapper;

@Mapper
// public interface StreamTaskMapper extends YtBaseMapper<StreamTaskDO> {
public interface StreamTaskMapper {

    public StreamTaskDO selectByJobId(Long id);

}
