package testableMock;

import com.alibaba.testable.core.annotation.TestableMock;
import com.alibaba.testable.processor.annotation.EnablePrivateAccess;

import org.junit.Test;

import testableMock.dto.Response;
import testableMock.service.WeatherApi;
import testableMock.service.impl.WeatherApiImpl;

@EnablePrivateAccess
public class Test1 {

     // new WeatherApiImpl();

    @TestableMock(targetMethod = "query")
    public Response query(WeatherApi self, String cityName) {
        Response response = new Response();
        response.setCityInfo(cityName + " weather is in mock");
        return response;
    }

    /**
     * 测试 private方法调用
     */
    @Test
    public void test_private() {

    }

}
