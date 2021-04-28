package com.novencia.bltech.posts.listener.impl;

import com.novencia.bltech.api.dto.RedditMediaPostDto;
import com.novencia.bltech.posts.listener.MediaPostStorageListener;
import com.novencia.bltech.posts.listener.events.NewMediaPostsAvailableEvent;
import com.novencia.bltech.posts.listener.events.NewSingleMediaPostAvailableEvent;
import com.novencia.bltech.posts.repository.MediaPostRepository;
import com.novencia.bltech.posts.storage.StorageService;
import com.novencia.bltech.posts.storage.exception.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Component
public class CompositeMediaPostStorageListener extends AbstractMediaPostStorageListener {

    private final List<MediaPostStorageListener> listeners;

    public CompositeMediaPostStorageListener(MediaPostRepository repository,
                                             StorageService storageService,
                                             ThreadPoolTaskScheduler scheduler,
                                             List<MediaPostStorageListener> listeners) {
        super(repository, storageService, scheduler);
        this.listeners = listeners;
    }

    @Override
    @Async
    @EventListener
    public void onNewMediaPostsAvailableEvent(NewMediaPostsAvailableEvent event) throws StorageException {
        String subreddit = storageService.createBucketIfNotExists(event.getSubreddit());
        log.debug("Received event with {} mediaposts for subreddit {}", event.getMediaPosts().size(), subreddit);

        for (RedditMediaPostDto mediaPostDto : event.getMediaPosts()) {
            if (mediaPostDto.isEmbed() || !StringUtils.hasText(mediaPostDto.getMediaUrl())) {
                // Skip embed => Logic not implemented
                continue;
            }
            MediaPostStorageListener matchingListener = listeners.stream()
                    .filter(listener -> listener.matches(mediaPostDto))
                    .findFirst()
                    .orElse(this);

            matchingListener.onNewMediaPostAvailable(new NewSingleMediaPostAvailableEvent(event.getSubreddit(), mediaPostDto, subreddit));
        }
    }

    @Override
    public void onNewMediaPostAvailable(NewSingleMediaPostAvailableEvent event) {
        uploadAndSaveMediaPost(event.getMediaPost(), event.getSubreddit(), event.getBucketName());
    }

    @Override
    public boolean matches(RedditMediaPostDto mediaPostDto) {
        return true;
    }

}
