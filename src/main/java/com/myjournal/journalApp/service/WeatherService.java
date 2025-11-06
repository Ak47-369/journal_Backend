package com.myjournal.journalApp.service;

import com.myjournal.journalApp.configuration.WeatherApiConfig;
import com.myjournal.journalApp.dto.weather.WeatherResponse;
import com.myjournal.journalApp.exception.WeatherServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class WeatherService {
    private final RestClient restClient;
    private final WeatherApiConfig weatherApiConfig;
    private final RedisService redisService;


    public WeatherService(@Qualifier("weatherApiClient") RestClient restClient, WeatherApiConfig weatherApiConfig, RedisService redisService) {
        this.restClient = restClient;
        this.weatherApiConfig = weatherApiConfig;
        this.redisService = redisService;
    }

    public WeatherResponse getWeather(String city) {
        try {
            WeatherResponse weatherResponse = redisService.get(city, WeatherResponse.class);
            if(weatherResponse != null)
                return weatherResponse;
            redisService.get(city, WeatherResponse.class);
            log.info("Cache Miss!, Fetching weather for city: {} from External API", city);
            weatherResponse = restClient.get()
                    .uri(weatherApiConfig.getUrl(), uriBuilder -> uriBuilder
                            .queryParam("access_key", weatherApiConfig.getKey())
                            .queryParam("query", city)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        String errorBody = response.getBody().toString();
                        log.error("Client Error from Weather API: {} - Body: {}", response.getStatusCode(), errorBody);
                        throw new WeatherServiceException("Client error from weather service: " + errorBody, null);
                    })
                    .body(WeatherResponse.class);
            redisService.set(city, weatherResponse, 10, TimeUnit.MINUTES);
            return weatherResponse;
        } catch (RestClientException e) {
            log.error("RestClient Error: {}", e.getMessage());
            throw new WeatherServiceException("Error communicating with weather API", e);
        }
    }
}
