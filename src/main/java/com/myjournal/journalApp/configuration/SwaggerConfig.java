package com.myjournal.journalApp.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myCustomConfig(){
        return new OpenAPI().info(
                new Info().title("Journal App")
                        .description("By Amit")
                        .version("1.0")
                        .summary("Journal App API's Documentation")
        );
    }
}
