package com.gaspar.personalmetadata.auth.data;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

public record AuthFlowResult(
        String username,
        String userId,
        AwsCredentialsProvider credentialsProvider
) {
}
