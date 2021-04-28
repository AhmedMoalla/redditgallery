package com.novencia.bltech.explorer.client.impl;

import com.novencia.bltech.api.dto.Sort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

import static com.novencia.bltech.explorer.client.RedditClient.DEFAULT_LIMIT;


/**
 * Wrapper for WebClient to have a nice fluent API when sending requests
 */
@Slf4j
@RequiredArgsConstructor
public class MediaPostRequester {

    public static final String SUBREDDIT_POSTS_URI = "/r/{subreddit}/{sort}";
    public static final String REQUESTED_TYPE = "links";
    public static final int MAX_LIMIT = 100;

    private static final class QueryParams {
        public static final String LIMIT = "limit";
        public static final String AFTER = "after";
        public static final String TYPE = "type";

        private QueryParams() {}
    }

    private final WebClient webClient;

    private int limit = 10;
    private String after = "";
    private Sort sort = Sort.NEW;

    public MediaPostRequester limit(int limit) {
        this.limit = Math.max(limit, DEFAULT_LIMIT);
        this.limit = Math.min(this.limit, MAX_LIMIT);
        return this;
    }

    public MediaPostRequester after(String after) {
        this.after = after;
        return this;
    }

    public MediaPostRequester sort(Sort sort) {
        this.sort = sort;
        return this;
    }

    public Mono<String> sendRequest(String subreddit) {
        return webClient
                .get()
                .uri(builder -> {
                    URI uri = buildURI(builder, subreddit);
                    log.debug("Sending GET to {}", uri);
                    return uri;
                })
                .retrieve()
                .bodyToMono(String.class)
                .doOnTerminate(this::clearRequester);
    }

    private URI buildURI(UriBuilder builder, String usernameOrSubreddit) {
        builder = builder.path(SUBREDDIT_POSTS_URI)
                .queryParam(QueryParams.LIMIT, limit)
                .queryParam(QueryParams.AFTER, after)
                .queryParam(QueryParams.TYPE, REQUESTED_TYPE);

        return builder.build(usernameOrSubreddit, sort.name().toLowerCase());
    }

    private void clearRequester() {
        limit = 10;
        after = "";
        sort = Sort.NEW;
    }
}