package com.hb;

import com.hb.dao.mappers.StreamJobMapper;
import com.hb.domain.streaming.entity.StreamingJobDO;
import com.hb.domain.streaming.query.StreamJobDalQuery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@SpringBootTest
@RunWith(SpringRunner.class)
public class Test1 {
    @Autowired
    StreamJobMapper streamJobMapper;

    @Test
    public void TestMappersSelect() {
        System.out.println("测试主键读取的Mappers");
        StreamingJobDO res = streamJobMapper.selectByPrimaryKey(60L);
        System.out.println(res);
    }

    @Test
    public void TestSelectByJobParam() {
        System.out.println("测试 类型和名称的Mappers 对应的sql");
        StreamJobDalQuery streamJobDalQuery =new StreamJobDalQuery();
        streamJobDalQuery.setType(1);
        List<StreamingJobDO> res = streamJobMapper.selectByJobParam(streamJobDalQuery);
        System.out.println("length: " + res.size());
        System.out.println(res);

    }

}
