package com.novencia.bltech.users.configuration;

import com.novencia.bltech.users.configuration.properties.IdpProperties;
import com.novencia.bltech.users.service.IdpService;
import com.novencia.bltech.users.service.impl.KeycloakIdpService;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@EnableConfigurationProperties(IdpProperties.class)
public class GatewayConfiguration {

    @Bean
    PasswordEncoder provideBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    IdpService provideIdpService(IdpProperties idpProperties) {
        log.info("Idp Properties: {}", idpProperties);
        String serverUrl = String.format("http://%s:%d/auth", idpProperties.getHost(), idpProperties.getPort());
        UsersResource userResource = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(idpProperties.getRealm())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(idpProperties.getClientId())
                .clientSecret(idpProperties.getClientSecret())
                .build()
                .realm(idpProperties.getRealm())
                .users();
        return new KeycloakIdpService(userResource);
    }

    @Bean
    public ModelMapper provideModelMapper() {
        return new ModelMapper();
    }

}
