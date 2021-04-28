package com.novencia.bltech.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(IdpCredentials.class)
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public List<ServerWebExchangeMatcher> unsecureRoutesMatcher(GatewayProperties properties, RouteLocator locator) {
        List<ServerWebExchangeMatcher> matchers = new ArrayList<>();
        log.info("Unsecure gateway paths:");
        for (RouteDefinition route : properties.getRoutes()) {
            Object unsecure = route.getMetadata().get("unsecure");
            if (unsecure != null && (boolean) unsecure) {
                String pattern = (String) route.getMetadata().get("unsecure_pattern");
                log.info("\t- {}", pattern);
                if (StringUtils.hasText(pattern)) {
                    matchers.add(pathMatchers(pattern));
                }
            }
        }
        matchers.add(pathMatchers("/token"));
        return matchers;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder, IdpCredentials creds) {
        return builder.routes()
                .route("token", r -> r.path("/token")
                        .filters(f ->
                                f.prefixPath("/auth/realms/reddit-gallery/protocol/openid-connect")
                                 .modifyRequestBody(String.class, String.class, MediaType.APPLICATION_FORM_URLENCODED_VALUE, addClientCredentials(creds)))
                        .uri("http://localhost:5555"))
        .build();
    }

    private RewriteFunction<String, String> addClientCredentials(IdpCredentials creds) {
        return (ServerWebExchange serverWebExchange, String body) -> {
            String credentials = String.format("&client_id=%s&client_secret=%s&grant_type=%s&scope=%s",
                    creds.getClientId(), creds.getClientSecret(), "password", "openid");
            return Mono.just(body + credentials);
        };
    }
}
