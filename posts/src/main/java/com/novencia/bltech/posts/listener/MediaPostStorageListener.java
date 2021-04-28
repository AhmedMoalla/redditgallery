package com.novencia.bltech.posts.listener;

import com.novencia.bltech.api.dto.RedditMediaPostDto;
import com.novencia.bltech.posts.listener.events.NewMediaPostsAvailableEvent;
import com.novencia.bltech.posts.listener.events.NewSingleMediaPostAvailableEvent;
import com.novencia.bltech.posts.storage.exception.StorageException;

public interface MediaPostStorageListener {
    void onNewMediaPostsAvailableEvent(NewMediaPostsAvailableEvent event) throws StorageException;
    void onNewMediaPostAvailable(NewSingleMediaPostAvailableEvent event);
    boolean matches(RedditMediaPostDto dto);
}
