package com.naghamtours.config;

import com.naghamtours.service.TourService;
import com.naghamtours.service.impl.TourServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public TourService tourService() {
        return new TourServiceImpl();
    }
}