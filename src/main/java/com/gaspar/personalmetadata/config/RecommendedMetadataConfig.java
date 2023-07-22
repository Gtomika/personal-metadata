package com.gaspar.personalmetadata.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Configuration
public class RecommendedMetadataConfig {

    private final List<String> recommendedConfig;

    public RecommendedMetadataConfig(@Value("${metadata.recommended}") String recommendedConfig) {
        this.recommendedConfig = Arrays.asList(recommendedConfig.split("\\|"));
    }

    public Collection<String> getRecommendedConfig() {
        return Collections.unmodifiableCollection(recommendedConfig);
    }
}
