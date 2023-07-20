package com.gaspar.personalmetadata.swing.frame;

import com.gaspar.personalmetadata.PersonalMetadataApplication;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Slf4j
public class MainFrame extends JFrame {

    private JLabel usernameField;
    private JPanel contentPane;
    private JPanel cardPanel;
    private JButton myMetadataButton;
    private JButton modifyMetadataButton;

    private MainFrameCardType currentMainFrameCardType;
    private CardLayout mainFrameCardLayout;

    public MainFrame(String username, JPanel myMetadataCard, JPanel modifyMetadataCard) {
        setContentPane(contentPane);
        setTitle("Personal Metadata");

        usernameField.setText(username);
        cardPanel.add(myMetadataCard, MainFrameCardType.MY_METADATA.name());
        cardPanel.add(modifyMetadataCard, MainFrameCardType.MODIFY_METADATA.name());
        mainFrameCardLayout = (CardLayout) cardPanel.getLayout();
        showMyMetadataCard();

        myMetadataButton.addActionListener((e) -> showMyMetadataCard());
        modifyMetadataButton.addActionListener((e) -> showModifyMetadataCard());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                log.info("The user has closed the main frame");
                dispose();
                PersonalMetadataApplication.shutdown(0);
            }
        });

        pack();
    }

    private void showMyMetadataCard() {
        if(currentMainFrameCardType == MainFrameCardType.MY_METADATA) {
            log.info("Already showing 'my metadata' card");
            return;
        }
        currentMainFrameCardType = MainFrameCardType.MY_METADATA;
        mainFrameCardLayout.show(cardPanel, MainFrameCardType.MY_METADATA.name());
        log.info("Showing 'my metadata' card");
    }

    private void showModifyMetadataCard() {
        if(currentMainFrameCardType == MainFrameCardType.MODIFY_METADATA) {
            log.info("Already showing 'modify metadata' card");
            return;
        }
        currentMainFrameCardType = MainFrameCardType.MODIFY_METADATA;
        mainFrameCardLayout.show(cardPanel, MainFrameCardType.MODIFY_METADATA.name());
        log.info("Showing 'modify metadata' card");
    }
}
