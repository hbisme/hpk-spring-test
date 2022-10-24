package priv.hb.sample.dao.mappers;

import com.github.pagehelper.Page;

import priv.hb.sample.dao.entity.StreamingJobDO;
import priv.hb.sample.dao.query.StreamJobDalQuery;

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
     * 多条件查询作业信息,测试动态SQL 'IF'
     * @param
     * @return
     */
    List<StreamingJobDO> selectByJobParam(StreamJobDalQuery streamJobDalQuery);


    /**
     * 多条件查询作业信息,测试动态SQL 'Choose' (if/else)
     * @param
     * @return
     */
    List<StreamingJobDO> testChoose(StreamJobDalQuery streamJobDalQuery);


    /**
     * 多条件查询作业信息,测试动态SQL 'foreach'
     * @param streamingJobDOS
     * @return
     */
    List<StreamingJobDO> testForeach(List<StreamingJobDO> streamingJobDOS);


    /**
     * selectByJobParam 的分页方法
     * @param streamJobDalQuery
     * @return
     */
    Page<StreamingJobDO> pageSelectByJobParam(StreamJobDalQuery streamJobDalQuery);


}
