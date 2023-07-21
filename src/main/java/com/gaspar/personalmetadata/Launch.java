package com.gaspar.personalmetadata;

import com.gaspar.personalmetadata.auth.AuthService;
import com.gaspar.personalmetadata.auth.data.AuthFlowResult;
import com.gaspar.personalmetadata.config.DynamodbConfig;
import com.gaspar.personalmetadata.config.GeneralAwsConfig;
import com.gaspar.personalmetadata.config.LoggedInUserConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Launch {

    private final AuthService authService;
    private final GeneralAwsConfig generalAwsConfig;
    private final LoggedInUserConfig loggedInUserConfig;
    private final MainFrameView mainFrameView;
    private final DynamodbConfig dynamodbConfig;

    /**
     * This is the startup flow of the application, which performs the
     * authentication flow, and if that is successful, opens the main window.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        log.info("Application started, beginning auth flow...");
        AuthFlowResult authFlowResult = authService.authFlow();
        log.info("Auth flow successful!");

        loggedInUserConfig.setUsername(authFlowResult.username());
        loggedInUserConfig.setUserId(authFlowResult.userId());
        generalAwsConfig.setCredentialsProvider(authFlowResult.credentialsProvider());
        log.info("User '{}' with ID '{}' logged in!", authFlowResult.username(), authFlowResult.userId());

        dynamodbConfig.createDynamodbClient(authFlowResult.credentialsProvider());
        log.info("DynamoDB client was created to use the received credentials");

        mainFrameView.showMainFrame();
        log.info("Main frame is visible, app ready to accept user input");
    }

}
