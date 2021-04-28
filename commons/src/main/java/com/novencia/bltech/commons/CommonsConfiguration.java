package com.novencia.bltech.commons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Slf4j
@Configuration
@ComponentScan(basePackages = "com.novencia.bltech.commons")
public class CommonsConfiguration {
    @Bean
    public CommandLineRunner initCommonsConfig(Environment env,
                                               @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) {
        boolean securityEnabled = Arrays.asList(env.getActiveProfiles()).contains("oauth");
        if (securityEnabled) {
            log.info("Issuer Uri: {}", issuerUri);
        }
        return args -> log.info("Init Commons configuration. Security: {}", securityEnabled);
    }
}
