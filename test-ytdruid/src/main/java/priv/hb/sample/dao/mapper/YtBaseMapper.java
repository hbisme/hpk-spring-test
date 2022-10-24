package priv.hb.sample.dao.mapper;


import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 封装通用的mapper操作,继承该接口的mapper可获得内置的单表操作相关方法
 * @param <T>
 */
public interface YtBaseMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
