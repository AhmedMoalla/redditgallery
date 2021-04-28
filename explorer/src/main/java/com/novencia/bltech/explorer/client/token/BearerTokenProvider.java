package com.novencia.bltech.explorer.client.token;

import com.novencia.bltech.explorer.client.configuration.RedditProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * Service responsible for retrieving and refreshing an access token.
 * An access token is required in each request in the Authorization Header.
 * The access token is retrieved on application startup and is refreshed
 * just before it expires.
 */
@Slf4j
@Service
public class BearerTokenProvider {

    public static final String ACCESS_TOKEN_URI = "/api/v1/access_token";
    public static final String GRANT_TYPE = "grant_type";
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    private final WebClient webClient;
    private final RedditProperties properties;

    private String token;
    private int timeToLiveInSeconds;
    private Instant tokenCreationTime;

    public BearerTokenProvider(WebClient.Builder builder, RedditProperties properties) {
        this.webClient = builder
                .baseUrl(properties.getRedditBaseUrl())
                .defaultHeader(HttpHeaders.USER_AGENT, properties.getUserAgent())
                .build();
        this.properties = properties;
    }

    public String getToken() {

        if (!StringUtils.hasText(token) || isTokenNearlyExpired()) {
            log.debug("Token is nearly expired. Refreshing token...");
            AccessTokenResponse newToken = obtainAccessToken().block();
            if (newToken != null) {
                setToken(newToken);
            } else {
                throw new IllegalStateException("Token could not be retrieved.");
            }
        }

        return token;
    }

    private Mono<AccessTokenResponse> obtainAccessToken() {
        return webClient.post()
                .uri(ACCESS_TOKEN_URI)
                .headers(headers -> headers.setBasicAuth(properties.getClientId(), properties.getClientSecret()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(GRANT_TYPE, GRANT_TYPE_PASSWORD)
                        .with(USERNAME, properties.getUsername())
                        .with(PASSWORD, properties.getPassword()))
                .retrieve()
                .bodyToMono(AccessTokenResponse.class)
                .doOnNext(tokenResponse -> log.info("Token obtained from Reddit API: {}", tokenResponse));
    }

    private void setToken(AccessTokenResponse accessToken) {
        tokenCreationTime = Instant.now();
        token = accessToken.getAccessToken();
        timeToLiveInSeconds = accessToken.getExpiresInSeconds();
    }

    private boolean isTokenNearlyExpired() {
        Instant now = Instant.now();
        Duration earlyFetchDuration = Duration.ofSeconds(properties.getCheckRefreshPeriod() + 1);
        Duration lifetime = Duration.between(tokenCreationTime, now)
                .plus(earlyFetchDuration);
        return lifetime.getSeconds() > timeToLiveInSeconds;
    }
}
