package com.gaspar.personalmetadata.auth.exception;

public class LoginFailedException extends Exception {

    public LoginFailedException(Exception cause) {
        super(cause.getMessage(), cause);
    }

    public LoginFailedException(String message) {
        super(message);
    }
}
