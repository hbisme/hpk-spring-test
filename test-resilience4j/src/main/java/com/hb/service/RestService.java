package com.hb.service;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author hubin
 * @date 2022年08月12日 09:57
 */
@Service
public class RestService {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
