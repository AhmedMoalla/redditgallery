package com.novencia.bltech.scraper.batch;

import com.novencia.bltech.api.dto.RedditMediaPostDto;
import com.novencia.bltech.scraper.batch.listener.ScraperBatchNotificationListener;
import com.novencia.bltech.scraper.batch.processor.MediaPostSplitterItemProcessor;
import com.novencia.bltech.scraper.batch.reader.ExplorerItemReader;
import com.novencia.bltech.scraper.batch.writer.NewMediaPostsMessagingItemWriter;
import com.novencia.bltech.scraper.dto.MediaPostListings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.List;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class ScraperBatchConfiguration {

    public final JobBuilderFactory jobBuilderFactory;
    public final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job scraperJob(Step step, ScraperBatchNotificationListener listener) {
        return jobBuilderFactory.get("scraperJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step)
                .end()
                .build();
    }

    @Bean
    public Step scrapePostsStep(ExplorerItemReader reader,
                                MediaPostSplitterItemProcessor processor,
                                NewMediaPostsMessagingItemWriter writer) {
        return stepBuilderFactory.get("scrapePosts")
                .<MediaPostListings, List<RedditMediaPostDto>>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    @Primary
    public JobLauncher asyncJobLauncher(JobRepository jobRepository) {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return jobLauncher;
    }
}
