package com.company.andy;

import com.company.andy.common.configuration.profile.EnableOnlyForIT;
import com.company.andy.support.TestingActorJwtDecoder;
import com.company.andy.support.testid.TestIdFilter;
import de.flapdoodle.embed.mongo.commands.MongodArguments;
import de.flapdoodle.embed.mongo.config.Storage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.resttestclient.autoconfigure.RestTestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.net.URI;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create;

@Slf4j
@NullMarked
@Configuration(proxyBeanMethods = false)
public class IntegrationTestConfiguration {

    // This enables transaction for Mongo requires replica set for transaction to work
    @Bean
    @Profile("it-embedded")
    MongodArguments mongodArguments() {
        return MongodArguments.builder()
                .replication(Storage.of("rs0", 1000))
                .build();
    }

    @Bean
    RestTestClientBuilderCustomizer restTestClientBuilderCustomizer() {
        return builder -> builder
                .requestInterceptor(TestIdFilter.testIdInterceptor())
                .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE);
    }

    // override AuthenticationManagerResolver for oauth2 resource server,
    // testingActorJwtDecoder works with IntegrationTest.authHeaderOf() to bypass the actual authentication but still maintain end-to-end HTTP integration testing
    @Bean
    @EnableOnlyForIT
    public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver(TestingActorJwtDecoder testingActorJwtDecoder) {
        return (_) -> new ProviderManager(new JwtAuthenticationProvider(testingActorJwtDecoder));
    }

    @Bean
    @Profile("it-local")
    public S3Client s3Client() {
        S3Client s3Client = S3Client.builder()
                .region(Region.of("us-east-1"))
                .forcePathStyle(true)
                .endpointOverride(URI.create("http://localhost:5125"))
                .credentialsProvider(create(AwsBasicCredentials.create("a-rustfs-access-key", "a-rustfs-secret-key"))).build();

        String preCreateBucketName = "integration-test-bucket";
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(preCreateBucketName).build());
        } catch (Exception e) {
            log.info("Bucket[{}] already exists, which is what required.", preCreateBucketName);
        }

        return s3Client;
    }

}
