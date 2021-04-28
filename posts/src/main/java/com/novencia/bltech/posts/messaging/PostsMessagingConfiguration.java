package com.novencia.bltech.posts.messaging;

import com.novencia.bltech.api.dto.NewMediaPosts;
import com.novencia.bltech.posts.listener.events.NewMediaPostsAvailableEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PostsMessagingConfiguration {

    private final ApplicationEventPublisher eventPublisher;

    @Bean
    public Consumer<NewMediaPosts> persistNewPosts() {
        return newMediaPosts -> {
            log.info("Received {} new posts from scraper", newMediaPosts.getMediaPosts().size());
            eventPublisher.publishEvent(new NewMediaPostsAvailableEvent(newMediaPosts.getSubreddit(), newMediaPosts.getMediaPosts()));
        };
    }
}
