package com.test.mybatis.dao.mappers;

import com.test.mybatis.dao.entity.TRedpillTag;

import java.util.List;

/**
 * @author hubin
 * @date 2022年03月08日 10:50 上午
 */
public interface TRedpillTagMapper {
    TRedpillTag getById(int id);

    List<TRedpillTag> getAll();



}
