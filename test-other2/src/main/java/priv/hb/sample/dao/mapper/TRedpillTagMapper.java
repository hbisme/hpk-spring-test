package priv.hb.sample.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import priv.hb.sample.dao.entity.TRedpillTagDO;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author hubin
 * @date 2022年03月09日 3:16 下午
 */
@Repository
public interface TRedpillTagMapper extends BaseMapper<TRedpillTagDO> {
    /**
     * 使用条件构造器的例子,不用写SQL语句
     * @param type
     * @return
     */
    default List<TRedpillTagDO> selectByType(@Param("type") String type) {
        final QueryWrapper<TRedpillTagDO> tRedpillTag = new QueryWrapper<TRedpillTagDO>().eq("type", type);
        return selectList(tRedpillTag);
    }


    /**
     * 原生的形式,需要在xml里写SQL语句
     * @param ids
     * @return
     */
    List<TRedpillTagDO> selectByIds(@Param("ids")Collection<Integer> ids);

    /**
     * 分页查询
     * @param page
     * @param createTime
     * @return
     */
    default IPage<TRedpillTagDO> selectPageByCreateTime(IPage<TRedpillTagDO> page, @Param("createTime") Date createTime) {
        return selectPage(page, new QueryWrapper<TRedpillTagDO>().gt("create_time", createTime));
    }

    default TRedpillTagDO getLast() {
        final TRedpillTagDO tRedpillTagDO = selectOne(new QueryWrapper<TRedpillTagDO>().orderByDesc("id").last("LIMIT 1"));
        return tRedpillTagDO;
    }

}
