package com.gaspar.personalmetadata.swing.auth;

import lombok.Getter;

import javax.swing.*;
import java.awt.event.*;

public class LoginDialog extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;

    @Getter
    private JTextField usernameField;

    @Getter
    private JPasswordField passwordField;

    @Getter
    private boolean cancelled;

    public LoginDialog() {
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

    private void onOK() {
        if(usernameField.getText().isBlank()) {
            usernameField.setText("Enter username (email)!");
            return;
        }
        if(passwordField.getPassword().length == 0) {
            return;
        }
        cancelled = false;
        dispose();
    }

    private void onCancel() {
        cancelled = true;
        dispose();
    }
}
