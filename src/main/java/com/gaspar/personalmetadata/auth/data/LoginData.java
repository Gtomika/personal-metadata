package com.gaspar.personalmetadata.auth.data;

import lombok.Builder;

/**
 * Created after successful login.
 */
public record LoginData(
        String username,
        String accessToken,
        String idToken
) {
}
