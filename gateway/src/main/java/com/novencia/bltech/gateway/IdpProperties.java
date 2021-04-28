package com.novencia.bltech.gateway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("reddit-gallery.idp")
public class IdpProperties {
    private String host;
    private int port;
    private String realm;
    private String clientId;
    private String clientSecret;
}
