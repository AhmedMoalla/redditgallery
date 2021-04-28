package com.novencia.bltech.scraper.messaging;

import lombok.Data;

@Data
public class ScraperJobStartMessage {
    private String subreddit;
}
