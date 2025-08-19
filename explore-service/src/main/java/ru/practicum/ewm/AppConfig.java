package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AppConfig {
    @Value("${app.name}")
    private String appName;

    @Bean
    public String applicationName() {
        return appName;
    }
}
