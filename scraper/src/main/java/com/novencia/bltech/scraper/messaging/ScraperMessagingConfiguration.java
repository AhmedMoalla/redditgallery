package com.novencia.bltech.scraper.messaging;

import com.novencia.bltech.api.dto.NewMediaPosts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class ScraperMessagingConfiguration {

    @Bean
    public Consumer<ScraperJobStartMessage> startScraperJob(JobLauncher jobLauncher, Job job) {
        return message -> {
            log.info("Received start job message: {}", message);
            Map<String, JobParameter> paramMap = new HashMap<>();
            paramMap.put("subreddit", new JobParameter(message.getSubreddit()));
            paramMap.put("creationTime", new JobParameter(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)));
            JobParameters parameters = new JobParameters(paramMap);
            try {
                jobLauncher.run(job, parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    @Bean
    public Sinks.Many<NewMediaPosts> postsSink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }

    @Bean
    public Supplier<Flux<NewMediaPosts>> newPostsScraped(Sinks.Many<NewMediaPosts> sink) {
        return sink::asFlux;
    }

}
