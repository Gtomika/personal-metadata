package com.gaspar.personalmetadata.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;

@Getter
@Configuration
public class GeneralAwsConfig {

    private final Region awsRegion;
    private final String accountId;

    @Setter
    private AwsCredentialsProvider credentialsProvider;

    public GeneralAwsConfig(
            @Value("${aws.region}") String awsRegion,
            @Value("${aws.account-id}")String accountId
    ) {
        this.awsRegion = Region.of(awsRegion);
        this.accountId = accountId;
    }
}
