package com.company.andy;

import de.flapdoodle.embed.mongo.commands.MongodArguments;
import de.flapdoodle.embed.mongo.config.Storage;
import org.springframework.boot.resttestclient.autoconfigure.RestTestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration(proxyBeanMethods = false)
public class IntegrationTestConfiguration {

    // This enables transaction for Mongo requires replica set for transaction to work
    @Bean
    @Profile("it")
    MongodArguments mongodArguments() {
        return MongodArguments.builder()
                .replication(Storage.of("rs0", 1000))
                .build();
    }

    @Bean
    RestTestClientBuilderCustomizer restTestClientBuilderCustomizer() {
        return builder -> builder.defaultHeader(ACCEPT, APPLICATION_JSON_VALUE);
    }

}
