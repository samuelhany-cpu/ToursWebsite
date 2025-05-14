package com.naghamtours.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.naghamtours.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {
    // Additional JPA configurations can be added here if needed
} 