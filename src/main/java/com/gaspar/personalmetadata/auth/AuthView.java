package com.gaspar.personalmetadata.auth;

import com.gaspar.personalmetadata.auth.data.CredentialsData;

public interface AuthView {

    CredentialsData promptUserLogin();

    String promptUserPasswordChange();

    void showLoginFailed();

    boolean askUserAboutQuickLogin();

}
