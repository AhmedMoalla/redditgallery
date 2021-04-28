package com.novencia.bltech.explorer.client.impl;

import com.novencia.bltech.api.dto.Sort;
import com.novencia.bltech.explorer.client.RedditClient;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Default RedditClient implementation. Uses MediaPostRequester to query reddit API
 */
@RequiredArgsConstructor
public class RedditMediaClientImpl implements RedditClient {

    private final MediaPostRequester requester;

    @Override
    public Mono<String> getPosts(String usernameOrSubreddit, Sort sort, int limit) {
        return requester
                .limit(limit)
                .sort(sort)
                .sendRequest(usernameOrSubreddit);
    }

    @Override
    public Mono<String> getPostsAfter(String usernameOrSubreddit, String postId, Sort sort, int limit) {
        return requester
                .after(postId)
                .limit(limit)
                .sort(sort)
                .sendRequest(usernameOrSubreddit);
    }

}
