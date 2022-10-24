package priv.hb.sample.utils;

import priv.hb.sample.vo.UserVo;
import priv.hb.sample.bo.UserBo;
import priv.hb.sample.dto.UserDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


/**
 * @author hubin
 * @date 2022年03月10日 10:06 上午
 */
@Mapper
public interface UserConvert {
    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    UserVo convertToVo(UserDto userDto);

    @Mapping(source="username", target = "name")  // 名称不一致的映射
    @Mapping(target = "password", ignore = true)  // 忽略某些字段
    UserBo convertToBo(UserDto userDto);
}
