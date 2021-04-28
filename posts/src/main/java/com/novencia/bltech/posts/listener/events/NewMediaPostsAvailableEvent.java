package com.novencia.bltech.posts.listener.events;

import com.novencia.bltech.api.dto.RedditMediaPostDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class NewMediaPostsAvailableEvent extends ApplicationEvent {

    @Getter
    private final List<RedditMediaPostDto> mediaPosts;
    
    public NewMediaPostsAvailableEvent(String subreddit, List<RedditMediaPostDto> mediaPosts) {
        super(subreddit);
        this.mediaPosts = mediaPosts;
    }

    public String getSubreddit() {
        return (String) getSource();
    }
}
