package com.novencia.bltech.explorer.function;


import com.novencia.bltech.api.dto.ExplorerFunctionParams;
import com.novencia.bltech.api.dto.Sort;
import com.novencia.bltech.explorer.client.RedditClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
@Component("explorer")
@RequiredArgsConstructor
public class ExplorerFunction implements Function<Mono<ExplorerFunctionParams>, Mono<String>> {
    private final RedditClient redditClient;

    @Override
    public Mono<String> apply(Mono<ExplorerFunctionParams> functionParams) {
        return functionParams
                .doOnNext(p -> log.info("Explorer function executing with params: {}", p))
                .flatMap(p -> getPosts(p.getSubreddit(), p.getAfter(), p.getSort(), p.getLimit()));
    }

    private Mono<String> getPosts(String subreddit, String after, Sort sort, int limit) {
        Mono<String> result = redditClient.getPosts(subreddit, sort, limit);
        if (StringUtils.hasText(after)) {
            result = redditClient.getPostsAfter(subreddit, after, sort, limit);
        }
        return result;
    }
}
