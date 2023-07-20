package com.gaspar.personalmetadata.swing.auth;

import com.gaspar.personalmetadata.auth.AuthView;
import com.gaspar.personalmetadata.auth.data.CredentialsData;
import com.gaspar.personalmetadata.auth.exception.LoginFailedException;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class AuthViewImpl implements AuthView {

    @Override
    public CredentialsData promptUserLogin() throws LoginFailedException {
        LoginDialog loginDialog = new LoginDialog();
        loginDialog.setVisible(true);
        //user uses the UI and closes it
        if(loginDialog.isCancelled()) {
            throw new LoginFailedException("Login dialog was cancelled");
        }
        return new CredentialsData(
                loginDialog.getUsernameField().getText(),
                new String(loginDialog.getPasswordField().getPassword()),
                false
        );
    }

    @Override
    public String promptUserPasswordChange() throws LoginFailedException {
        PasswordChangeDialog passwordChangeDialog = new PasswordChangeDialog();
        passwordChangeDialog.setVisible(true);
        //user uses the UI and closes it
        if(passwordChangeDialog.isCancelled()) {
            throw new LoginFailedException("Password change dialog was cancelled");
        }
        return new String(passwordChangeDialog.getNewPasswordField().getPassword());
    }

    @Override
    public void showLoginFailed(String message) {
        JOptionPane.showMessageDialog(null, message, "Login error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public boolean askUserAboutQuickLogin() {
        int result = JOptionPane.showConfirmDialog(
                null,
                "Save login details for quick login?",
                "Quick login",
                JOptionPane.YES_NO_OPTION
        );
        return result == JOptionPane.YES_OPTION;
    }
}
