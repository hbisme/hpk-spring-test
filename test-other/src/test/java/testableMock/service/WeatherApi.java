package testableMock.service;

import testableMock.dto.Response;

public interface WeatherApi {
    Response query(String cityCode);

    public String queryShangHaiWeather();

}
