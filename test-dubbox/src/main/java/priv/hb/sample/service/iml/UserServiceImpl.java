package priv.hb.sample.service.iml;

import priv.hb.sample.service.UserService;
import priv.hb.sample.utils.CommonUtils;
import com.yt.ustone.api.user.UserQueryApi;
// import com.yt.ustone.api.user.UserIdNumApi;
import com.yt.ustone.domain.ResultData;
import com.yt.ustone.domain.to.BasicUserCacheTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserQueryApi userQueryApi;


    @Override
    public String test0() {
        return "test0";
    }

    @Override
    public ResultData<BasicUserCacheTO> test1() {
        ResultData<BasicUserCacheTO> res = userQueryApi.getBasicUserById("6121");
        System.out.println(res);
        return res;
    }

    @Override
    public String testStatic0() {
        return CommonUtils.getTest0();
    }
}
