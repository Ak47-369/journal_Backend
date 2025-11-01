package com.myjournal.journalApp.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Current {
    private int temperature;
    @JsonProperty("weather_descriptions")
    private List<String> weatherDescriptions;
    private int feelslike;

}
