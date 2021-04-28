package com.novencia.bltech.users.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("reddit-gallery.idp")
public class IdpProperties {
    private String host;
    private int port;
    private String realm;
    private String clientId;
    private String clientSecret;
}
