package com.gaspar.personalmetadata.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
@RequiredArgsConstructor
public class DynamodbConfig {

    private final GeneralAwsConfig awsConfig;

    @Bean
    @Lazy
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(awsConfig.getAwsRegion())
                .credentialsProvider(awsConfig.getCredentialsProvider())
                .build();
    }

}
