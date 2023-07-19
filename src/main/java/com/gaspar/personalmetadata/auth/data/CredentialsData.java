package com.gaspar.personalmetadata.auth.data;

import java.util.Map;

/**
 * Created before login and used to perform the login.
 */
public record CredentialsData(
        String username,
        String password,
        boolean isQuickLogin
) {

    public Map<String, String> toAuthParameters() {
        return Map.of(
                "USERNAME", username,
                "PASSWORD", password
        );
    }

}
