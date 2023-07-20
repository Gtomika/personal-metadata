package com.gaspar.personalmetadata.repo;

public record MetadataHead(
        String userId,
        String fileId,
        String lastKnownPath,
        String createdAt
) {
}
