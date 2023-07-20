package com.gaspar.personalmetadata.repo;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.fromN;
import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.fromS;

@Component
public class MetadataItemMapper {

    private static final String USER_ID = "UserId";
    private static final String FILE_ID = "FileId";
    private static final String LAST_KNOWN_PATH = "LastKnownPath";
    private static final String CREATED_AT = "CreatedAt";
    private static final List<String> HEAD_ATTRIBUTES = List.of(USER_ID, FILE_ID, LAST_KNOWN_PATH, CREATED_AT);

    public Map<String, AttributeValue> metadataToItem(Metadata metadata) {
        Map<String, AttributeValue> item = new HashMap<>(metadataHeadToItem(metadata.head()));
        for(var attribute: metadata.attributes().entrySet()) {
            item.put(attribute.getKey(), fromS(attribute.getValue()));
        }
        return item;
    }

    public Map<String, AttributeValue> metadataHeadToItem(MetadataHead head) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(USER_ID, fromS(head.userId()));
        item.put(FILE_ID, fromS(head.fileId()));
        item.put(LAST_KNOWN_PATH, fromS(head.lastKnownPath()));
        item.put(CREATED_AT, fromN(head.createdAt()));
        return item;
    }

    public Metadata itemToMetadata(Map<String, AttributeValue> item) {
        MetadataHead head = itemToMetadataHead(item);
        Map<String, String> attributes = new HashMap<>();
        for(var attribute: item.entrySet()) {
            if(!isHeadAttribute(attribute.getKey())) {
                attributes.put(attribute.getKey(), attribute.getValue().s());
            }
        }
        return new Metadata(head, attributes);
    }

    public MetadataHead itemToMetadataHead(Map<String, AttributeValue> item) {
        return new MetadataHead(
                item.get(USER_ID).s(),
                item.get(FILE_ID).s(),
                item.get(LAST_KNOWN_PATH).s(),
                item.get(CREATED_AT).n()
        );
    }

    public Map<String, AttributeValue> toKey(String userId, String fileId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(USER_ID, fromS(userId));
        key.put(FILE_ID, fromS(fileId));
        return key;
    }

    public boolean isHeadAttribute(String attributeName) {
        return HEAD_ATTRIBUTES.contains(attributeName);
    }

    public String headAttributesProjectionExpression() {
        return String.join(",", HEAD_ATTRIBUTES);
    }

    public String KeyConditionExpression(String userId) {
        return USER_ID + " = :" + userId;
    }


}
