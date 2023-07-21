package com.gaspar.personalmetadata.swing.frame;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Slf4j
@Lazy
@Component
public class LoadingPanel extends JPanel {

    private JPanel contentPane;

    public LoadingPanel() {
        add(contentPane);
        contentPane.setVisible(false);
    }

    public void showLoading() {
        contentPane.setVisible(true);
        contentPane.repaint();
        log.info("Showing loading indicator");
    }

    public void hideLoading() {
        contentPane.setVisible(false);
        contentPane.repaint();
        log.info("Hiding loading indicator");
    }
}
