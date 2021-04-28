package com.novencia.bltech.scraper.batch.writer;

import com.novencia.bltech.api.dto.RedditMediaPostDto;
import com.novencia.bltech.api.dto.NewMediaPosts;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NewMediaPostsMessagingItemWriter implements ItemWriter<List<RedditMediaPostDto>> {

    private final Sinks.Many<NewMediaPosts> sink;

    @Override
    public void write(List<? extends List<RedditMediaPostDto>> list) {
        List<RedditMediaPostDto> mediaPosts = list.get(0);
        String subreddit = mediaPosts.get(0).getSubreddit();
        NewMediaPosts newMediaPosts = new NewMediaPosts(subreddit, mediaPosts);
        sink.tryEmitNext(newMediaPosts).orThrow();
    }
}
