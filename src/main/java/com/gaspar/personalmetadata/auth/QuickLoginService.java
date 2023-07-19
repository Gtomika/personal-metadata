package com.gaspar.personalmetadata.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaspar.personalmetadata.auth.data.CredentialsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Quick login is when the username and password are saved so that repeated
 * application starts are faster.
 */
@Slf4j
@Service
public class QuickLoginService {

    private final Path quickLoginFilePath;
    private final ObjectMapper objectMapper;

    public QuickLoginService(
            @Value("${auth.quick-login.file-path}") String quickLoginFilePath,
            ObjectMapper objectMapper
    ) {
        this.quickLoginFilePath = Paths.get(quickLoginFilePath);
        this.objectMapper = objectMapper;
    }

    /**
     * Attempt to read the quick login data.
     * @return Empty optional is returned if the file is not found or cannot be read.
     */
    public Optional<CredentialsData> readQuickLoginData() {
        try {
            if(Files.exists(quickLoginFilePath)) {
                return Optional.of(objectMapper.readValue(
                        quickLoginFilePath.toFile(),
                        CredentialsData.class
                ));
            } else {
                log.info("The quick login file does not exist, used must be prompted for credentials");
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Failed to read the quick login file at '{}'", quickLoginFilePath, e);
            return Optional.empty();
        }
    }

    public void writeQuickLoginData(CredentialsData quickCredentialsData) {
        try {
            objectMapper.writeValue(quickLoginFilePath.toFile(), quickCredentialsData);
        } catch (Exception e) {
            log.error("Failed to write the quick login data to '{}'", quickLoginFilePath, e);
        }
    }
}
