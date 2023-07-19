package com.gaspar.personalmetadata.auth;

import com.gaspar.personalmetadata.PersonalMetadataApplication;
import com.gaspar.personalmetadata.auth.data.AuthFlowResult;
import com.gaspar.personalmetadata.auth.data.CredentialsData;
import com.gaspar.personalmetadata.auth.data.LoginData;
import com.gaspar.personalmetadata.auth.exception.LoginFailedException;
import com.gaspar.personalmetadata.config.CognitoConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.GetCredentialsForIdentityRequest;
import software.amazon.awssdk.services.cognitoidentity.model.GetIdRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.Map;

/**
 * The auth service is responsible for the main authentication (login) flow.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int EXIT_CODE_AUTH_FAILED = -1;

    private final QuickLoginService quickLoginService;
    private final AuthView authView;
    private final CognitoIdentityProviderClient cognitoUserClient;
    private final CognitoIdentityClient cognitoIdentityClient;
    private final CognitoConfig cognitoConfig;

    public AuthFlowResult authFlow() {
        CredentialsData credentialsData = obtainCredentials();
        try {
            final LoginData loginData = login(credentialsData);
            final String userId = getCognitoUserId(loginData);
            AwsSessionCredentials awsCredentials = exchangeUserIdForAwsCredentials(loginData, userId);

            if(!credentialsData.isQuickLogin() && authView.askUserAboutQuickLogin()) {
                quickLoginService.writeQuickLoginData(credentialsData);
            }

            return new AuthFlowResult(
                    credentialsData.username(),
                    userId,
                    StaticCredentialsProvider.create(awsCredentials)
            );
        } catch (LoginFailedException e) {
            log.error("Fatal error in login process. Cannot open application.", e);
            authView.showLoginFailed();
            PersonalMetadataApplication.shutdown(EXIT_CODE_AUTH_FAILED);
            return null;
        }

    }

    private CredentialsData obtainCredentials() {
        var quickLoginData = quickLoginService.readQuickLoginData();
        if(quickLoginData.isPresent()) {
            log.info("Credentials have been obtained from quick login");
            return quickLoginData.get();
        } else {
            return authView.promptUserLogin();
        }
    }

    private LoginData login(CredentialsData credentialsData) throws LoginFailedException {
        InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(credentialsData.toAuthParameters())
                .clientId(cognitoConfig.getUserPoolClientId())
                .build();
        try {
            InitiateAuthResponse authResponse = cognitoUserClient.initiateAuth(authRequest);
            if(authResponse.challengeName() == ChallengeNameType.NEW_PASSWORD_REQUIRED) {
                return changePassword(credentialsData.username(), authResponse.session());
            }
            return new LoginData(
                    credentialsData.username(),
                    authResponse.authenticationResult().accessToken(),
                    authResponse.authenticationResult().idToken()
            );
        } catch (SdkClientException e) {
            log.error("The login has failed with the given credentials", e);
            throw new LoginFailedException(e);
        }
    }

    private LoginData changePassword(String username, String session) {
        String newPassword = authView.promptUserPasswordChange();
        var challengeRequest = RespondToAuthChallengeRequest.builder()
                .clientId(cognitoConfig.getUserPoolClientId())
                .session(session)
                .challengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                .challengeResponses(Map.of(
                        "USERNAME", username,
                        "NEW_PASSWORD", newPassword
                ))
                .build();
        var challengeResponse = cognitoUserClient.respondToAuthChallenge(challengeRequest);
        return new LoginData(
                username,
                challengeResponse.authenticationResult().accessToken(),
                challengeResponse.authenticationResult().idToken()
        );
    }

    private AwsSessionCredentials exchangeUserIdForAwsCredentials(LoginData loginData, String userId) throws LoginFailedException {
        try {
            var awsCredsRequest = GetCredentialsForIdentityRequest.builder()
                    .identityId(userId)
                    .logins(Map.of(
                            cognitoConfig.getLoginIdpName(), loginData.idToken()
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
            log.error("The login has failed, could not exchange to AWS credentials", e);
            throw new LoginFailedException(e);
        }
    }

    private String getCognitoUserId(LoginData loginData) throws LoginFailedException {
        try {
            var idRequest = GetIdRequest.builder()
                    .accountId(cognitoConfig.getAwsConfig().getAccountId())
                    .identityPoolId(cognitoConfig.getIdentityPoolId())
                    .logins(Map.of(
                            cognitoConfig.getLoginIdpName(), loginData.idToken()
                    ))
                    .build();
            var idResponse = cognitoIdentityClient.getId(idRequest);
            return idResponse.identityId();
        } catch (SdkClientException e) {
            log.error("The login has failed, could not exchange to Cognito user ID", e);
            throw new LoginFailedException(e);
        }
    }

}
