package com.novencia.bltech.posts.listener.impl;

import com.novencia.bltech.api.dto.RedditMediaPostDto;
import com.novencia.bltech.posts.listener.events.NewSingleMediaPostAvailableEvent;
import com.novencia.bltech.posts.repository.MediaPostRepository;
import com.novencia.bltech.posts.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GalleryMediaPostStorageListener extends AbstractMediaPostStorageListener {

    public GalleryMediaPostStorageListener(MediaPostRepository repository,
                                           StorageService storageService,
                                           ThreadPoolTaskScheduler scheduler) {
        super(repository, storageService, scheduler);
    }

    @Override
    public void onNewMediaPostAvailable(NewSingleMediaPostAvailableEvent event) {
        log.debug("Gallery media post available {}", event);
        RedditMediaPostDto mediaPostDto = event.getMediaPost();
        Map<String, String> mediaUrls = mediaPostDto.getGalleryMediaUrls();
        if (mediaUrls == null || mediaUrls.isEmpty()) {
            log.warn("Gallery Media Post: {} was skipped because gallery media urls was empty", mediaPostDto);
            return;
        }

        List<RedditMediaPostDto> dtos = splitGalleryToMediaPosts(mediaPostDto, mediaUrls);
        dtos.forEach(dto -> uploadAndSaveMediaPost(dto, event.getSubreddit(), event.getBucketName()));
    }

    private List<RedditMediaPostDto> splitGalleryToMediaPosts(RedditMediaPostDto parentDto, Map<String, String> mediaUrls) {
        return mediaUrls.entrySet()
                .stream()
                .map(entry -> {
                    RedditMediaPostDto dto = parentDto.toBuilder().build();
                    dto.setId(entry.getKey());
                    dto.setMediaUrl(entry.getValue());
                    dto.setMediaThumbnailUrl(entry.getValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean matches(RedditMediaPostDto mediaPostDto) {
        return mediaPostDto.isGallery();
    }
}
