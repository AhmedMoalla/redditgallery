package com.novencia.bltech.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExplorerFunctionParams {
    private String subreddit;
    private int limit;
    private String after;
    private Sort sort = Sort.NEW;
}
