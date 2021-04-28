package com.novencia.bltech.posts.storage.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("reddit-gallery.minio")
public class MinioClientProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
}
