package com.novencia.bltech.scraper.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScraperBatchNotificationListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Starting scraper batch (jobId: {}) with parameters: {}", jobExecution.getJobId(), jobExecution.getJobParameters());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Scraper batch (jobId: {}) finished with status: {}", jobExecution.getJobId(), jobExecution.getStatus());
    }
}
