package com.myjournal.journalApp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    @Bean
    public RestClient restClient(){
        // The builder can be used to set default headers, base urls etc.
        return RestClient.builder().build();
    }
}
