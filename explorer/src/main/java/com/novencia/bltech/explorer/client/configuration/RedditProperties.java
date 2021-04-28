package com.novencia.bltech.explorer.client.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("reddit-client")
public class RedditProperties {
    private String redditBaseUrl = "https://www.reddit.com";
    private String redditOAuthBaseUrl = "https://oauth.reddit.com";
    private String username;
    private String password;
    private String clientId;
    private String clientSecret;

    /**
     * Duration in seconds indicating the interval on which a check is done to see if the token needs to be refreshed
     * Default 15 Minutes
     */
    private long checkRefreshPeriod = 15 * 60L;
    private String userAgent = "RedditClient (Novencia)";
}
