package com.hiperium.city.tasks.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

import java.net.URI;
import java.util.Objects;

@Configuration
public class DynamoDBConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDBConfig.class);

    @Value("${aws.dynamodb.endpoint-override}")
    private String dynamoDBEndpoint;

    @Value("${aws.region}")
    private String region;

    @Bean
    public DynamoDbAsyncClient dynamoDbAsyncClient() {
        LOGGER.info("AWS region: {}", this.region);
        LOGGER.info("DynamoDB endpoint: {}", this.dynamoDBEndpoint);
        DynamoDbAsyncClientBuilder dynamoDbClientBuilder = DynamoDbAsyncClient.builder()
                .region(Region.of(this.region))
                .credentialsProvider(ProfileCredentialsProvider.create());
        if (Objects.nonNull(this.dynamoDBEndpoint) && !this.dynamoDBEndpoint.isBlank()) {
            dynamoDbClientBuilder.endpointOverride(URI.create(this.dynamoDBEndpoint));
        }
        return dynamoDbClientBuilder.build();
    }
}
