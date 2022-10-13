package com.hb.mapStruct.mapper;

import com.hb.mapStruct.pojo.Order;
import com.hb.mapStruct.pojo.OrderQueryParam;

import org.mapstruct.Mapper;

@Mapper
public interface OrderMapper {
    OrderQueryParam entity2queryParam(Order order);

}
