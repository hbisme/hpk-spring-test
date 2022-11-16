package priv.hb.sample.service;

import java.util.List;

import com.yangt.ucenter.vo.dataaccess.DataAccessListVO;
import com.yt.ustone.domain.ResultData;
import com.yt.ustone.domain.to.BasicUserCacheTO;

public interface UserService {
    String test0();

    ResultData<BasicUserCacheTO> test1();


    List<DataAccessListVO> testUcenter();
}
