package com.myjournal.journalApp.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This class provides a type-safe way to access the weather API configuration properties
 * defined in application.yml under the "weather.api" prefix.
 */
@Configuration
@ConfigurationProperties(prefix = "weather.api")
@Getter
@Setter
public class WeatherApiConfig {

    /**
     * The base URL of the Weatherstack API.
     * This will be populated from the 'weather.api.url' property.
     */
    private String url;

    /**
     * The secret access key for the Weatherstack API.
     * This will be populated from the 'weather.api.key' property.
     */
    private String key;

}
