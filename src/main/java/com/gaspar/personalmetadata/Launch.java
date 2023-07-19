package com.gaspar.personalmetadata;

import com.gaspar.personalmetadata.auth.AuthService;
import com.gaspar.personalmetadata.auth.data.AuthFlowResult;
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

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        log.info("Application started, beginning auth flow...");
        AuthFlowResult result = authService.authFlow();
        log.info("Auth flow successful!");

        loggedInUserConfig.setUsername(result.username());
        loggedInUserConfig.setUserId(result.userId());
        generalAwsConfig.setCredentialsProvider(result.credentialsProvider());
        log.info("Application initialized, user logged in!");
    }

}
