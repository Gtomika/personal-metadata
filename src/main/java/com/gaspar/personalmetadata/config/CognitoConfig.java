package com.gaspar.personalmetadata.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Getter
@Configuration
public class CognitoConfig {

    private final GeneralAwsConfig awsConfig;
    private final String userPoolId;
    private final String userPoolClientId;
    private final String identityPoolId;
    private final String loginIdpName;

    public CognitoConfig(
            GeneralAwsConfig awsConfig,
            @Value("${aws.cognito.user-pool-id}") String userPoolId,
            @Value("${aws.cognito.user-pool-client-id}") String userPoolClientId,
            @Value("${aws.cognito.identity-pool-id}") String identityPoolId
    ) {
        this.awsConfig = awsConfig;
        this.userPoolId = userPoolId;
        this.userPoolClientId = userPoolClientId;
        this.identityPoolId = identityPoolId;
        this.loginIdpName = "cognito-idp." + awsConfig.getAwsRegion().id() + ".amazonaws.com/" + userPoolId;
    }

    @Bean
    public CognitoIdentityProviderClient cognitoUserClient() {
        return CognitoIdentityProviderClient.builder()
                .region(awsConfig.getAwsRegion())
                .build();
    }

    @Bean
    public CognitoIdentityClient cognitoIdentityClient() {
        return CognitoIdentityClient.builder()
                .region(awsConfig.getAwsRegion())
                .build();
    }

}
