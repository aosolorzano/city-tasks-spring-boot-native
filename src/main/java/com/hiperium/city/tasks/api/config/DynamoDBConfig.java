package com.hiperium.city.tasks.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

import java.net.URI;
import java.util.Objects;

@Configuration
@EnableConfigurationProperties(DynamoDBConfig.AwsProperties.class)
public class DynamoDBConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDBConfig.class);

    @ConfigurationProperties(prefix = "aws")
    record AwsProperties(String endpointOverride, String region, String accessKeyId, String secretAccessKey) {}

    @Bean
    public DynamoDbAsyncClient dynamoDbAsyncClient(AwsProperties awsProperties) {
        LOGGER.info("Endpoint Override: {}", awsProperties.endpointOverride);
        LOGGER.info("Region: {}", awsProperties.region);
        DynamoDbAsyncClientBuilder dynamoDbClientBuilder = DynamoDbAsyncClient.builder()
                .region(Region.of(awsProperties.region));
        if (Objects.isNull(awsProperties.accessKeyId) || Objects.isNull(awsProperties.secretAccessKey)) {
            dynamoDbClientBuilder.credentialsProvider(EnvironmentVariableCredentialsProvider.create());
        } else {
            dynamoDbClientBuilder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(awsProperties.accessKeyId, awsProperties.secretAccessKey)
            ));
        }
        if (Objects.nonNull(awsProperties.endpointOverride) && !awsProperties.endpointOverride.isBlank()) {
            dynamoDbClientBuilder.endpointOverride(URI.create(awsProperties.endpointOverride));
        }
        return dynamoDbClientBuilder.build();
    }
}
