package com.novencia.bltech.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewMediaPosts {
    private String subreddit;
    private List<RedditMediaPostDto> mediaPosts;
}
