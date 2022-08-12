package com.hb.service;




import com.hb.dao.UserDao;
import com.hb.dataobject.UserDO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public UserDO get(Integer id) {
        return userDao.selectById(id);
    }

}