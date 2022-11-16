package priv.hb.sample.service.iml;

import java.util.List;

import priv.hb.sample.service.UserService;

import com.yangt.ucenter.api.dataaccess.DataAccessApi;
import com.yangt.ucenter.query.dataaccess.DataAccessQuery;
import com.yangt.ucenter.vo.dataaccess.DataAccessListVO;
import com.yt.asd.kit.domain.RpcResult;
import com.yt.ustone.api.user.UserQueryApi;
// import com.yt.ustone.api.user.UserIdNumApi;
import com.yt.ustone.domain.ResultData;
import com.yt.ustone.domain.to.BasicUserCacheTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    // @Autowired
    // UserQueryApi userQueryApi;

    @Autowired
    DataAccessApi dataAccessApi;


    @Override
    public String test0() {
        return "test0";
    }

    @Override
    public ResultData<BasicUserCacheTO> test1() {
        // ResultData<BasicUserCacheTO> res = userQueryApi.getBasicUserById("6121");res = null;
        ResultData<BasicUserCacheTO> res = null;
        System.out.println(res);
        return res;
    }

    @Override
    public List<DataAccessListVO> testUcenter() {
        DataAccessQuery query = new DataAccessQuery();
        query.setName(null);
        query.setPageNo(1);
        query.setPageSize(10);
        RpcResult<List<DataAccessListVO>> rpcResult;
        try {
            rpcResult = dataAccessApi.dataAccessPage(query);
        } catch (Exception ex) {
            throw new RuntimeException(
                    String.format("调用接口失败 name=%s, pageNo=%s, pageSize=%s", null, 1, 10), ex);
        }
        List<DataAccessListVO> data = rpcResult.getData();
        return data;


    }
}
