package com.myjournal.journalApp.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // To Do - Global Configuration
// During Deserialization(JSON to POJO), if some json field not present in POJO, ignore it
public class WeatherResponse {

    // This field name MUST match the key in the JSON: "current"
    private Current current;

}
