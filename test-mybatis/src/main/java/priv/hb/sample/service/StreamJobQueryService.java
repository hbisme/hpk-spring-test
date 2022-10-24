package priv.hb.sample.service;

import com.github.pagehelper.Page;
import priv.hb.sample.dao.entity.StreamingJobDO;
import priv.hb.sample.dao.query.StreamJobDalQuery;

import java.util.List;

public interface StreamJobQueryService {

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


    /**
     * selectByJobParam 的分页方法
     * @param streamJobDalQuery
     * @return
     */
    Page<StreamingJobDO> pageSelectByJobParam(StreamJobDalQuery streamJobDalQuery);
}
