package com.novencia.bltech.gateway;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("reddit-gallery.idp")
public class IdpCredentials {
    private String realm;
    private String clientId;
    private String clientSecret;
}
