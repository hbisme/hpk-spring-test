package com.hb.service.iml;

import com.hb.service.UserService;
import com.yt.ustone.api.user.UserQueryApi;
import com.yt.ustone.domain.ResultData;
import com.yt.ustone.domain.to.BasicUserCacheTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserQueryApi userQueryApi;

    @Override
    public ResultData<BasicUserCacheTO> test1() {
        ResultData<BasicUserCacheTO> res = userQueryApi.getBasicUserById("6121");
        System.out.println(res);
        return res;
    }
}
