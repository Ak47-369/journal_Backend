package com.myjournal.journalApp;

import com.myjournal.journalApp.configuration.JwtConfig;
import com.myjournal.journalApp.configuration.RateLimiterSlidingWindowConfig;
import com.myjournal.journalApp.configuration.RateLimiterTokenBucketConfig;
import com.myjournal.journalApp.configuration.WeatherApiConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableCaching
@EnableScheduling
@EnableConfigurationProperties({
        WeatherApiConfig.class,
        RateLimiterSlidingWindowConfig.class,
        RateLimiterTokenBucketConfig.class,
        JwtConfig.class
})
@EnableMongoAuditing // Now everytime you save() and entity , createdAt and updatedAt fields populated automatically
//@EnableTransactionManagement - Spring automatically setups it, after detecting spring-boot-starter-data-mongodb
public class JournalApplication {

	public static void main(String[] args) {
		SpringApplication.run(JournalApplication.class, args);
	}

}
