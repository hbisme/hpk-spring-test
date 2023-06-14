package priv.hb.sample;

import java.util.List;


import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TestName;

import com.yangt.devkit.dubbo.DubboRefer;
import com.yangt.devkit.dubbo.DubboReferFactory;
import com.yangt.devkit.dubbo.DubboReferRule;
import com.yangt.octopus.api.MetaEnumApi;
import com.yangt.octopus.common.dto.EnumBaseInfo;
import com.yangt.ucenter.api.dataaccess.DataAccessApi;
import com.yangt.ucenter.query.dataaccess.DataAccessQuery;
import com.yangt.ucenter.vo.dataaccess.DataAccessListVO;
import com.yt.asd.kit.domain.RpcResult;

/**
 * dubbo泛化调用测试.
 * @author hubin
 * @date 2022年11月16日 14:51
 */
public class DubboReferRuleTest {
    // static DubboReferFactory referFactory = new DubboReferFactory("testkit");

    // public DubboReferRule referRule = referFactory.createReferRule();

    DubboRefer referRule = new DubboReferFactory("testkit").createRefer();


    @Test
    public void test1() {
        DataAccessApi dataAccessApi = referRule.refService(DataAccessApi.class);
        DataAccessQuery query = new DataAccessQuery();
        query.setName(null);
        query.setPageNo(1);
        query.setPageSize(10);
        RpcResult<List<DataAccessListVO>> rpcResult;
        rpcResult = dataAccessApi.dataAccessPage(query);
        System.out.println(rpcResult);

    }


    @Test
    public void test2() {
        MetaEnumApi metaEnumApi = referRule.refService(MetaEnumApi.class);

        RpcResult<List<EnumBaseInfo>> rpcResult = metaEnumApi.getEnumByListCode(io.vavr.collection.List.of("shop_type").toJavaList());
        System.out.println("rpcResult: " +rpcResult);

    }


}
