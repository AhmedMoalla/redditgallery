package com.novencia.bltech.scraper.dto;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novencia.bltech.api.dto.RedditMediaPostDto;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class MediaPostListings {
    @Getter
    private final List<RedditMediaPostDto> mediaPosts = new ArrayList<>();
    @Getter
    private String after;

    @JacksonInject
    private ObjectMapper objectMapper;

    @JsonProperty("data")
    private void extractFromData(JsonNode data) {
        // mediaPosts
        JsonNode children = data.get("children");
        StreamSupport.stream(children.spliterator(), false)
                .map(node -> node.get("data"))
                .map(this::toMediaPost)
                .forEach(mediaPosts::add);

        // after
        after = data.get("after").asText();
    }

    @SneakyThrows
    private RedditMediaPostDto toMediaPost(JsonNode node) {
        return objectMapper.treeToValue(node, RedditMediaPostDto.class);
    }
}