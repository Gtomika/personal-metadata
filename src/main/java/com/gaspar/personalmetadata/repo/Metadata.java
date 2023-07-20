package com.gaspar.personalmetadata.repo;

import java.util.Map;

public record Metadata(
        MetadataHead head,
        Map<String, String> attributes
) {
}
