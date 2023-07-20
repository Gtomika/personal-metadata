package com.gaspar.personalmetadata.auth;

import com.gaspar.personalmetadata.auth.data.CredentialsData;
import com.gaspar.personalmetadata.auth.exception.LoginFailedException;

public interface AuthView {

    CredentialsData promptUserLogin() throws LoginFailedException;

    String promptUserPasswordChange() throws LoginFailedException;

    void showLoginFailed(String message);

    boolean askUserAboutQuickLogin();

}
