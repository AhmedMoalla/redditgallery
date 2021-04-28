package com.novencia.bltech.scraper.batch.processor;

import com.novencia.bltech.api.dto.RedditMediaPostDto;
import com.novencia.bltech.scraper.dto.MediaPostListings;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MediaPostSplitterItemProcessor implements ItemProcessor<MediaPostListings, List<RedditMediaPostDto>> {
    @Override
    public List<RedditMediaPostDto> process(MediaPostListings listings) throws Exception {
        return listings.getMediaPosts();
    }
}
