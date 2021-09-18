package com.hb;

import com.github.pagehelper.Page;
import com.hb.dao.mappers.StreamJobMapper;
import com.hb.dao.entity.StreamingJobDO;
import com.hb.dao.query.StreamJobDalQuery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.pagehelper.PageHelper;
import com.hb.service.StreamJobQueryService;
import com.yt.asd.kit.domain.RpcResult;

import java.util.List;


@SpringBootTest
@RunWith(SpringRunner.class)
public class OriginalMybatisTest {
    @Autowired
    StreamJobMapper streamJobMapper;

    @Autowired
    StreamJobQueryService streamJobQueryService;

    @Test
    public void TestMappersSelect() {
        System.out.println("测试主键读取的Mappers");
        StreamingJobDO res = streamJobMapper.selectByPrimaryKey(60L);
        System.out.println(res);
    }

    @Test
    public void TestSelectByJobParam() {
        System.out.println("测试 类型和名称的Mappers 对应的sql");
        StreamJobDalQuery streamJobDalQuery = new StreamJobDalQuery();
        streamJobDalQuery.setType(1);
        List<StreamingJobDO> res = streamJobMapper.selectByJobParam(streamJobDalQuery);
        System.out.println("length: " + res.size());
        System.out.println(res);
    }

    /**
     * 分页插件使用, 使用 pagehelper-spring-boot-starter, 使用默认配置
     * 起始页从1开始计数.
     */
    @Test
    public void testPageHelper() {
        PageHelper.startPage(2, 2, true);
        StreamJobDalQuery streamJobDalQuery = new StreamJobDalQuery();
        streamJobDalQuery.setType(1);
        Page<StreamingJobDO> page = streamJobMapper.pageSelectByJobParam(streamJobDalQuery);
        List<StreamingJobDO> res = page.getResult();
        System.out.println("res: " + res);
    }

    /**
     * 分页插件使用, 使用 pagehelper-spring-boot-starter, 使用默认配置
     */
    @Test
    public void testPageHelper2() {
        StreamJobDalQuery streamJobDalQuery = new StreamJobDalQuery();
        streamJobDalQuery.setType(1);
        Page<StreamingJobDO> page = streamJobQueryService.pageSelectByJobParam(streamJobDalQuery);
        List<StreamingJobDO> res = page.getResult();
        System.out.println(res);
    }

    /**
     * 分页对象后,赋值给包装对象
     */
    @Test
    public void testPageHelper3() {
        StreamJobDalQuery streamJobDalQuery = new StreamJobDalQuery();
        streamJobDalQuery.setType(1);
        Page<StreamingJobDO> page = streamJobQueryService.pageSelectByJobParam(streamJobDalQuery);
        List<StreamingJobDO> list = page.getResult();
        System.out.println(list);

        RpcResult<List<StreamingJobDO>> result = new RpcResult<>();
        result.setData(list);
        result.setTotalCount(Long.valueOf(list.size()));
        System.out.println(result.getData());
        // System.out.println(result);
    }


}
