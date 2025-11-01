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

@Service
@Slf4j
public class WeatherService {
    private final RestClient restClient;
    private final WeatherApiConfig weatherApiConfig;


    public WeatherService(@Qualifier("weatherApiClient") RestClient restClient, WeatherApiConfig weatherApiConfig) {
        this.restClient = restClient;
        this.weatherApiConfig = weatherApiConfig;
    }

    public WeatherResponse getWeather(String city) {
        try {
            return restClient.get()
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
        } catch (RestClientException e) {
            log.error("RestClient Error: {}", e.getMessage());
            throw new WeatherServiceException("Error communicating with weather API", e);
        }
    }
}
