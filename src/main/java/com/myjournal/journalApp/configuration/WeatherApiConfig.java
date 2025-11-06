package com.myjournal.journalApp.configuration;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * This class provides a type-safe way to access the weather API configuration properties
 * defined in application.yml under the "weather.api" prefix.
 */
@ConfigurationProperties(prefix = "weather.api")
@Getter
@Setter
@Validated
public class WeatherApiConfig {

    /**
     * The base URL of the Weatherstack API.
     * This will be populated from the 'weather.api.url' property.
     */
    @NotBlank(message = "API URL cannot be blank")
    @URL(message = "API URL must be a valid URL")
    private String url;

    /**
     * The secret access key for the Weatherstack API.
     * This will be populated from the 'weather.api.key' property.
     */
    @NotBlank(message = "API key cannot be blank")
    private String key;

}
