package com.cts.batch.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3Service {
	@Autowired
	private AmazonS3 s3client;

	@Value("${cts.s3.bucket}")
	private String bucketName;

	@Value("${cts.s3.uploadfile}")
	private String uploadFilePath;
	
	
	@Value("${cts.s3.key}")
	private String keyName;

	public void uploadFile() {

		try {

			File file = new File(uploadFilePath);
			s3client.putObject(new PutObjectRequest(bucketName, keyName, file));
			log.info("===================== Upload File - Done! =====================");

		} catch (AmazonServiceException ase) {
			log.info("Error Message:={},HTTP Status Code={},AWS Error Code={},Error Type={},Request ID={}",ase.getMessage(),ase.getStatusCode(),ase.getErrorCode(),ase.getErrorType(),ase.getRequestId());
		} catch (AmazonClientException ace) {
			log.info("Caught an AmazonClientException: ");
			log.info("Error Message: " + ace.getMessage());
		}
	}

}
