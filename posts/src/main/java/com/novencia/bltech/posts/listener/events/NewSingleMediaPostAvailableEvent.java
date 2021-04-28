package com.novencia.bltech.posts.listener.events;

import com.novencia.bltech.api.dto.RedditMediaPostDto;
import lombok.Getter;

public class NewSingleMediaPostAvailableEvent {
    @Getter
    private final RedditMediaPostDto mediaPost;
    @Getter
    private final String subreddit;
    @Getter
    private final String bucketName;

    public NewSingleMediaPostAvailableEvent(String subreddit, RedditMediaPostDto mediaPost, String bucketName) {
        this.mediaPost = mediaPost;
        this.subreddit = subreddit;
        this.bucketName = bucketName;
    }
}
