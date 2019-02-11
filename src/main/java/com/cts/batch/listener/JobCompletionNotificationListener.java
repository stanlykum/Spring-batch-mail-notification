package com.cts.batch.listener;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cts.batch.service.S3Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
	@Autowired
	private S3Service s3Service;

	@Override
	public void afterJob(JobExecution jobExecution) {

		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			s3Service.uploadFile();
			log.info("!!! JOB FINISHED! File Uploaded to S3 successfully");

		}
	}
}