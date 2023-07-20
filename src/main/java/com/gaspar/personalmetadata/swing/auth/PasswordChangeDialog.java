package com.gaspar.personalmetadata.swing.auth;

import lombok.Getter;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class PasswordChangeDialog extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;

    @Getter
    private JPasswordField newPasswordField;

    private JPasswordField newPasswordAgainField;
    private JLabel errorField;

    @Getter
    private boolean cancelled;

    public PasswordChangeDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(e -> onOK());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        setLocationRelativeTo(null);
        pack();
    }

    private void onCancel() {
        cancelled = true;
        dispose();
    }

    private void onOK() {
        if(anyPasswordEmpty()) {
            errorField.setText("Fill both fields!");
            return;
        }
        if(!passwordsMatch()) {
            errorField.setText("Password and confirmation do not match!");
            return;
        }
        if(checkPasswordFulfillsRequirements()) {
            return;
        }
        cancelled = false;
        dispose();
    }

    private boolean anyPasswordEmpty() {
        return newPasswordField.getPassword().length == 0 || newPasswordAgainField.getPassword().length == 0;
    }

    private boolean passwordsMatch() {
        return Arrays.equals(newPasswordField.getPassword(), newPasswordAgainField.getPassword());
    }

    private boolean checkPasswordFulfillsRequirements() {
        String password = new String(newPasswordField.getPassword());
        if(password.length() < 8) {
            errorField.setText("Password is too short!");
            return true;
        }
        if(password.chars().noneMatch(Character::isLowerCase)) {
            errorField.setText("Password must contain lower case letter");
            return true;
        }
        if(password.chars().noneMatch(Character::isUpperCase)) {
            errorField.setText("Password must contain upper case letter");
            return true;
        }
        if(password.chars().noneMatch(Character::isDigit)) {
            errorField.setText("Password must contain number");
            return true;
        }
        return false;
    }

}
