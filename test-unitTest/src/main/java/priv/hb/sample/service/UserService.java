package priv.hb.sample.service;




import priv.hb.sample.dao.UserDao;
import priv.hb.sample.dataobject.UserDO;

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