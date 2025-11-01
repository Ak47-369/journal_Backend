package com.myjournal.journalApp.service;

import com.myjournal.journalApp.dto.weather.WeatherResponse;
import com.myjournal.journalApp.exception.WeatherServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
@Slf4j
public class WeatherService {
    private final RestClient weatherApiClient;

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${weather.api.key}")
    private String apiKey;

    // Use @Qualifier to specify which bean to inject
    public WeatherService(@Qualifier("weatherApiClient") RestClient weatherApiClient) {
        this.weatherApiClient = weatherApiClient;
    }

    public WeatherResponse getWeather(String city) {
        try {
            return weatherApiClient.get()
                    .uri(apiUrl, uriBuilder -> uriBuilder
                            .queryParam("access_key", apiKey)
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
