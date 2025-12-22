package com.codingnomads.demo_web.clients.dtos;

import lombok.Data;

@Data
public class WeatherResponse {

    private String type;
    private Properties properties;

}
