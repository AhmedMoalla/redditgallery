package com.novencia.bltech.explorer.client;

import com.novencia.bltech.api.dto.Sort;
import reactor.core.publisher.Mono;

public interface RedditClient {

    int DEFAULT_LIMIT = 10;
    Mono<String> getPosts(String usernameOrSubreddit, Sort sort, int limit);
    Mono<String> getPostsAfter(String usernameOrSubreddit, String postId, Sort sort, int limit);
}
