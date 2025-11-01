package com.myjournal.journalApp.service;

import com.myjournal.journalApp.dto.weather.WeatherResponse;
import com.myjournal.journalApp.exception.WeatherServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final RestClient restClient;

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${weather.api.key}")
    private String apiKey;

    public WeatherService(RestClient restClient) {
        this.restClient = restClient;
    }

    public WeatherResponse getWeather(String city) {
        try {
            return restClient.get()
                    .uri(apiUrl, uriBuilder -> uriBuilder
                            .queryParam("access_key", apiKey)
                            .queryParam("query", city)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        String errorBody = response.getBody().toString();
                        logger.error("Client Error from Weather API: {} - Body: {}", response.getStatusCode(), errorBody);
                        throw new WeatherServiceException("Client error from weather service: " + errorBody, null);
                    })
                    .body(WeatherResponse.class);
        } catch (RestClientException e) {
            logger.error("RestClient Error: {}", e.getMessage());
            throw new WeatherServiceException("Error communicating with weather API", e);
        }
    }
}
