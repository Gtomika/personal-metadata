package com.gaspar.personalmetadata.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamodbConfig {

    private final GeneralAwsConfig awsConfig;

    @Getter
    private final String metadataTableName;

    public DynamodbConfig(
            GeneralAwsConfig awsConfig,
            @Value("${aws.dynamodb.metadata-table-name}") String metadataTableName
    ) {
        this.awsConfig = awsConfig;
        this.metadataTableName = metadataTableName;
    }

    @Bean
    @Lazy
    //@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(awsConfig.getAwsRegion())
                .credentialsProvider(awsConfig.getCredentialsProvider())
                .build();
    }

}
