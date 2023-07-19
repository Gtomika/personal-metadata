package com.gaspar.personalmetadata.auth;

import com.gaspar.personalmetadata.config.CognitoConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.GetCredentialsForIdentityRequest;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CustomCognitoCredentialsProvider implements AwsCredentialsProvider {

    private final String idToken;
    private final String userId;
    private final CognitoConfig cognitoConfig;
    private final CognitoIdentityClient cognitoIdentityClient;

    @Override
    public AwsCredentials resolveCredentials() {
        try {
            var awsCredsRequest = GetCredentialsForIdentityRequest.builder()
                    .identityId(userId)
                    .logins(Map.of(
                            cognitoConfig.getLoginIdpName(), idToken
                    ))
                    .build();

            var awsCredsResponse = cognitoIdentityClient.getCredentialsForIdentity(awsCredsRequest);
            return AwsSessionCredentials.builder()
                    .accessKeyId(awsCredsResponse.credentials().accessKeyId())
                    .secretAccessKey(awsCredsResponse.credentials().secretKey())
                    .sessionToken(awsCredsResponse.credentials().sessionToken())
                    .expirationTime(awsCredsResponse.credentials().expiration())
                    .build();
        } catch (SdkClientException e) {
            log.error("Could not exchange to AWS credentials", e);
            throw new RuntimeException("Could not exchange to AWS credentials");
        }
    }

}
