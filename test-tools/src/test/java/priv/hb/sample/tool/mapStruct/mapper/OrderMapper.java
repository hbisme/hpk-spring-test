package priv.hb.sample.tool.mapStruct.mapper;

import priv.hb.sample.tool.mapStruct.pojo.Order;
import priv.hb.sample.tool.mapStruct.pojo.OrderQueryParam;

import org.mapstruct.Mapper;

@Mapper
public interface OrderMapper {
    OrderQueryParam entity2queryParam(Order order);

}
