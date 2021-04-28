package com.novencia.bltech.posts.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Document("media_post")
public class MediaPost {

    @Id
    @NotEmpty
    private String id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String objectId;
    @NotEmpty
    private String thumbnailObjectId;
    @NotNull
    private LocalDateTime creationDateTime;
    @NotNull
    private String subreddit;
}
