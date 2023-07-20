package com.gaspar.personalmetadata.repo;

import com.gaspar.personalmetadata.config.DynamodbConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Lazy
@Repository
@RequiredArgsConstructor
public class MetadataRepository {

    private static final String CREATED_AT_INDEX = "CreatedAtIndex";

    private final MetadataItemMapper mapper;
    private final DynamodbConfig dynamodbConfig;

    public Metadata getMetadata(String userId, String fileId) {
        GetItemRequest getRequest = GetItemRequest.builder()
                .tableName(dynamodbConfig.getMetadataTableName())
                .key(mapper.toKey(userId, fileId))
                .build();
        GetItemResponse getResponse = dynamodbConfig.getDynamoDbClient().getItem(getRequest);

        //it is assumed that the app only allows this for existing items
        if(getResponse.hasItem()) {
            return mapper.itemToMetadata(getResponse.item());
        } else {
            log.error("Get item operation found no item!");
            return null;
        }
    }

    public void putMetadata(Metadata metadata) {
        PutItemRequest putRequest = PutItemRequest.builder()
                .tableName(dynamodbConfig.getMetadataTableName())
                .item(mapper.metadataToItem(metadata))
                .build();
        dynamodbConfig.getDynamoDbClient().putItem(putRequest);
    }

    public void deleteMetadata(String userId, String fileId) {
        DeleteItemRequest deleteRequest = DeleteItemRequest.builder()
                .tableName(dynamodbConfig.getMetadataTableName())
                .key(mapper.toKey(userId, fileId))
                .build();
        dynamodbConfig.getDynamoDbClient().deleteItem(deleteRequest);
    }

}
