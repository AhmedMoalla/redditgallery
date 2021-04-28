package com.novencia.bltech.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RedditMediaPostDto {
    private String id;
    private String mediaUrl;
    private String mediaThumbnailUrl;
    private String username;
    private String subreddit;
    private String title;
    private boolean isEmbed;
    private String embedHtml;
    private String embedProviderName;
    private String creationDateTime;
    private boolean isGallery;
    private Map<String, String> galleryMediaUrls;
}
