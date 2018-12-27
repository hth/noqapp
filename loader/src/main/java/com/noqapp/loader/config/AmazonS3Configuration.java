package com.noqapp.loader.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * hitender
 * 5/28/18 9:17 AM
 */
@Configuration
public class AmazonS3Configuration {

    @Value("${aws.s3.accessKey}")
    private String accessKey;

    @Value("${aws.s3.secretKey}")
    private String secretKey;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Bean
    public AmazonS3 getS3client() {
        final ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProtocol(Protocol.HTTPS);

        final AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withClientConfiguration(clientConfiguration)
            .withRegion(Regions.AP_SOUTH_1)
            .build();

        Assert.isTrue(s3client.doesBucketExistV2(bucketName), "bucketName " + bucketName + " exists");
        return s3client;
    }
}
