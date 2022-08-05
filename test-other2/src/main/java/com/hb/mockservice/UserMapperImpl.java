package com.hb.mockservice;

import org.springframework.stereotype.Service;

/**
 * @author hubin
 * @date 2022年08月04日 11:05
 */
@Service
public class UserMapperImpl implements UserMapper{
    @Override
    public User selectOne(Integer id) {
        return null;
    }

    @Override
    public void print() {
        System.out.println("test");
    }



}
