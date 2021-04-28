package com.novencia.bltech.scraper.batch.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novencia.bltech.api.dto.ExplorerFunctionParams;
import com.novencia.bltech.api.dto.RedditMediaPostDto;
import com.novencia.bltech.api.dto.Sort;
import com.novencia.bltech.scraper.dto.MediaPostListings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@StepScope
public class ExplorerItemReader implements ItemReader<MediaPostListings> {

    private static final int MAX_LIMIT = 100;

    private final ExplorerFunctionClient explorer;
    private final ObjectMapper objectMapper;
    private final ExplorerFunctionParams params;

    private boolean hasMore = true;
    private String nextAfter = null;

    public ExplorerItemReader(ExplorerFunctionClient explorer,
                              ObjectMapper objectMapper,
                              @Value("#{jobParameters['subreddit']}") String subreddit) {
        this.explorer = explorer;
        this.objectMapper = objectMapper;
        params = new ExplorerFunctionParams(subreddit, MAX_LIMIT, null, Sort.NEW);
    }

    @Override
    public MediaPostListings read() throws ParseException {

        if (hasMore) {
            params.setAfter(nextAfter);
            log.info("Explorer has more data for params '{}'", params);
            String result = explorer.getPosts(params);
            MediaPostListings listings;
            try {
                log.debug("Received json: {}", result);
                listings = objectMapper.readValue(result, MediaPostListings.class);
            } catch (Exception e) {
                throw new ParseException("Exception while parsing listings json", e);
            }
            List<RedditMediaPostDto> mediaPosts = listings.getMediaPosts();
            if (mediaPosts != null && !mediaPosts.isEmpty()) {
                hasMore = mediaPosts.size() == 100;
                nextAfter = listings.getAfter();
                return listings;
            } else if (mediaPosts != null) {
                hasMore = false;
                nextAfter = null;
                log.info("No more data from Explorer for subreddit '{}'", params.getSubreddit());
                return null; // End reader if no more posts
            }
        }

        log.info("No more data from Explorer for subreddit '{}'", params.getSubreddit());
        return null;
    }
}
