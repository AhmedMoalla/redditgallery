package com.novencia.bltech.posts.listener.impl;

import com.novencia.bltech.api.dto.RedditMediaPostDto;
import com.novencia.bltech.posts.entity.MediaPost;
import com.novencia.bltech.posts.listener.MediaPostStorageListener;
import com.novencia.bltech.posts.listener.events.NewMediaPostsAvailableEvent;
import com.novencia.bltech.posts.repository.MediaPostRepository;
import com.novencia.bltech.posts.storage.StorageService;
import com.novencia.bltech.posts.storage.exception.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

@Slf4j
public abstract class AbstractMediaPostStorageListener implements MediaPostStorageListener {

    protected final MediaPostRepository repository;
    protected final StorageService storageService;
    private final ThreadPoolTaskScheduler scheduler;

    protected AbstractMediaPostStorageListener(MediaPostRepository repository,
                                               StorageService storageService,
                                               ThreadPoolTaskScheduler scheduler) {
        this.repository = repository;
        this.storageService = storageService;
        this.scheduler = scheduler;
    }

    @Override
    public void onNewMediaPostsAvailableEvent(NewMediaPostsAvailableEvent event) throws StorageException {
        // Do not override. Only used by CompositeMediaPostStorageListener as an @EventListener
    }

    protected void uploadAndSaveMediaPost(RedditMediaPostDto mediaPostDto, String subreddit, String bucketName) {

        String mediaUrl = mediaPostDto.getMediaUrl();
        String mediaThumbnailUrl = mediaPostDto.getMediaThumbnailUrl();

        try (InputStream tempMediaData = new URL(mediaUrl).openStream()) {
            byte[] bytes = tempMediaData.readAllBytes();
            InputStream mediaData = new ByteArrayInputStream(bytes);
            InputStream thumbnailMediaData = new URL(mediaThumbnailUrl).openStream();

            MediaPost mediaPost = mapToEntity(mediaPostDto, subreddit);
            String mediaFileName = extractFileNameFromUrl(mediaUrl);
            if (!StringUtils.hasText(mediaFileName)) {
                log.warn("Skipped media post {} because fileName was empty", mediaPost);
                return;
            }
            String mediaContentType = Files.probeContentType(Paths.get(mediaFileName));
            String thumbnailFileName = extractFileNameFromUrl(mediaThumbnailUrl);
            String thumbnailContentType = Files.probeContentType(Paths.get(thumbnailFileName));

            if (mediaContentType == null) {
                mediaContentType = Files.probeContentType(Paths.get(thumbnailFileName));
            }
            if (thumbnailContentType == null) {
                thumbnailContentType = Files.probeContentType(Paths.get(mediaFileName));
            }

            String finalMediaContentType = mediaContentType;
            String finalThumbContentType = thumbnailContentType;

            CompletableFuture<Void> mediaFuture = submitUploadTask(() ->
                    mediaPost.setObjectId(storageService.uploadMediaToStorage(mediaData, mediaFileName, bucketName, finalMediaContentType)));
            CompletableFuture<Void> thumbnailFuture = submitUploadTask(() ->
                    mediaPost.setThumbnailObjectId(storageService.uploadMediaToStorage(thumbnailMediaData, thumbnailFileName, bucketName, finalThumbContentType)));
            CompletableFuture.allOf(mediaFuture, thumbnailFuture)
                    .thenAcceptAsync((Void res) -> {
                        log.debug("Saving mediaPost {}", mediaPost);
                        repository.save(mediaPost).subscribe();
                    });
        } catch (Exception e) {
            log.error("Error happened while trying to upload media post {} to bucket {}", mediaPostDto, bucketName, e);
        }
    }

    private String extractFileNameFromUrl(String mediaUrl) {
        String fileName = StringUtils.getFilename(mediaUrl);
        if (fileName != null && fileName.contains("?")) {
            fileName = fileName.split("\\?")[0];
        }
        return fileName;
    }

    private CompletableFuture<Void> submitUploadTask(UploadTask uploadTask) {
        return scheduler.submitListenable(() -> {
            try {
                uploadTask.run();
            } catch (StorageException e) {
                log.error("Error happened while trying to upload media", e);
                return null;
            }
            return (Void) null;
        }).completable();
    }

    protected MediaPost mapToEntity(RedditMediaPostDto mediaPostDto, String subreddit) {
        MediaPost mediaPost = new MediaPost();
        mediaPost.setId(mediaPostDto.getId());
        mediaPost.setTitle(mediaPostDto.getTitle());
        mediaPost.setCreationDateTime(LocalDateTime.from(ISO_DATE_TIME.parse(mediaPostDto.getCreationDateTime())));
        mediaPost.setSubreddit(subreddit);
        return mediaPost;
    }

    interface UploadTask {
        void run() throws StorageException;
    }
}
