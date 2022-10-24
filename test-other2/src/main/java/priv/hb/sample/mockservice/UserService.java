package priv.hb.sample.mockservice;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hubin
 * @date 2022年08月04日 11:06
 */
public class UserService {
    @Autowired
    UserMapper userMapper;

    public User getOne(Integer id)
    {
        return userMapper.selectOne(id);
    }
}
