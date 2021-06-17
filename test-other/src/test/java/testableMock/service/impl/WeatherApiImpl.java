package testableMock.service.impl;

import testableMock.dto.Response;
import testableMock.service.WeatherApi;

public class WeatherApiImpl implements WeatherApi {
    @Override
    public Response query(String cityCode) {
        String cityInfo = cityCode + " 好天气";
        Response response = new Response();
        response.setCityInfo(cityInfo);
        return response;
    }

    public String queryShangHaiWeather() {
        return "查询天气: " + query("上海");
    }

}
