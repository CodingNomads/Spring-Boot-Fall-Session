package com.codingnomads.demo_web.clients;

import com.codingnomads.demo_web.clients.dtos.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class WeatherClient {

    private final RestTemplate restTemplate;

    public WeatherResponse getWeather(int a, int b) {
//        ResponseEntity<WeatherResponse> result = restTemplate.exchange(
//                "https://api.weather.gov/gridpoints/TOP/32,81/forecast",
//                HttpMethod.GET,
//                null,
//                WeatherResponse.class
//        );

        ResponseEntity<WeatherResponse> result = restTemplate.getForEntity(
                "https://api.weather.gov/gridpoints/TOP/" + a + "," + b + "/forecast",
                WeatherResponse.class
        );

        if (!result.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error getting weather");
        }

        return result.getBody();
    }
}
