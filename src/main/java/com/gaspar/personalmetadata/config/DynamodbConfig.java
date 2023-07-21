package com.gaspar.personalmetadata.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamodbConfig {

    private final GeneralAwsConfig awsConfig;

    @Getter
    private final String metadataTableName;

    @Getter
    private final String createdAtIndexName;

    @Getter
    private DynamoDbClient dynamoDbClient;

    public DynamodbConfig(
            GeneralAwsConfig awsConfig,
            @Value("${aws.dynamodb.metadata-table-name}") String metadataTableName,
            @Value("${aws.dynamodb.created-at-index-name}") String createdAtIndexName
    ) {
        this.awsConfig = awsConfig;
        this.metadataTableName = metadataTableName;
        this.createdAtIndexName = createdAtIndexName;
    }

    public void createDynamodbClient(AwsCredentialsProvider credentialsProvider) {
        dynamoDbClient = DynamoDbClient.builder()
                .region(awsConfig.getAwsRegion())
                .credentialsProvider(credentialsProvider)
                .build();
    }

}
