package com.myjournal.journalApp.configuration;

import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myCustomConfig() {
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local Development Server");

        Server productionServer = new Server()
                .url("https://journalbackend-production.up.railway.app")
                .description("Production Server");

        return new OpenAPI()
                .info(
                        new Info().title("Journal App")
                                .description("By Amit")
                                .version("1.0")
                                .summary("Journal App API's Documentation")
                )
                .servers(List.of(localServer, productionServer));
    }
}
