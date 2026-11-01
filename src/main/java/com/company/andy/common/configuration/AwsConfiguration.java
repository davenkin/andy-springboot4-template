package com.company.andy.common.configuration;

import com.company.andy.common.configuration.profile.DisableForLocal;
import com.company.andy.common.configuration.property.AwsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.net.URI;

import static software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class AwsConfiguration {

    @Bean
    @DisableForLocal
    public S3Client s3Client(AwsProperties awsProperties) {
        return S3Client.builder()
                .region(Region.of(awsProperties.s3().region()))
                .credentialsProvider(DefaultCredentialsProvider.builder().build()).build();
    }

    @Bean
    @Profile("local | local-it")
    public S3Client localS3Client(AwsProperties awsProperties) {
        S3Client s3Client = S3Client.builder()
                .region(Region.of(awsProperties.s3().region()))
                .forcePathStyle(true)
                .endpointOverride(URI.create("http://localhost:5125"))
                .credentialsProvider(create(AwsBasicCredentials.create("rustfsaccesskey", "rustfssecretkey"))).build();

        String preCreateBucketName = awsProperties.s3().bucket();
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(preCreateBucketName).build());
        } catch (Exception e) {
            log.info("Bucket[{}] already exists, no need to create.", preCreateBucketName);
        }

        return s3Client;
    }
}
