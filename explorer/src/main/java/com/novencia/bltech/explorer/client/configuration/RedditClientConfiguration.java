package com.novencia.bltech.explorer.client.configuration;

import com.novencia.bltech.explorer.client.RedditClient;
import com.novencia.bltech.explorer.client.impl.MediaPostRequester;
import com.novencia.bltech.explorer.client.impl.RedditMediaClientImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnProperty("reddit-client.client-id")
@EnableConfigurationProperties(RedditProperties.class)
public class RedditClientConfiguration {

    @Bean
    RedditClient provideRedditClient(WebClient webClient) {
        MediaPostRequester requester = new MediaPostRequester(webClient);
        return new RedditMediaClientImpl(requester);
    }
}