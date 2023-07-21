package com.gaspar.personalmetadata.swing.frame;

import com.gaspar.personalmetadata.PersonalMetadataApplication;
import com.gaspar.personalmetadata.config.LoggedInUserConfig;
import com.gaspar.personalmetadata.swing.LoadingPanelView;
import com.gaspar.personalmetadata.swing.ModifyMetadataCardView;
import com.gaspar.personalmetadata.swing.SelectFileCardView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
@Lazy
@Component
public class MainFrame extends JFrame {

    private JLabel usernameField;
    private JPanel contentPane;
    private JPanel cardPanel;
    private JPanel loadingPanelHolder;

    private MainFrameCardType currentMainFrameCardType;
    private final CardLayout mainFrameCardLayout;

    public MainFrame(
            LoggedInUserConfig loggedInUserConfig,
            SelectFileCardView selectFileCardView,
            ModifyMetadataCardView modifyMetadataCardView,
            LoadingPanelView loadingPanelView
    ) {
        setContentPane(contentPane);
        setTitle("Personal Metadata");

        usernameField.setText(loggedInUserConfig.getUsername());
        selectFileCardView.attachSelectFileCard(cardPanel, MainFrameCardType.SELECT_FILE.name());
        modifyMetadataCardView.attachModifyMetadataCard(cardPanel, MainFrameCardType.MODIFY_METADATA.name());
        mainFrameCardLayout = (CardLayout) cardPanel.getLayout();
        showSelectFileCard();

        loadingPanelView.attachLoadingPanel(loadingPanelHolder, BorderLayout.CENTER);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                log.info("The user has closed the main frame");
                dispose();
                PersonalMetadataApplication.shutdown(0);
            }
        });

        setLocationRelativeTo(null);
        pack();
    }

    public void showSelectFileCard() {
        if(currentMainFrameCardType == MainFrameCardType.SELECT_FILE) {
            log.info("Already showing 'my metadata' card");
            return;
        }
        currentMainFrameCardType = MainFrameCardType.SELECT_FILE;
        mainFrameCardLayout.show(cardPanel, MainFrameCardType.SELECT_FILE.name());
        log.info("Showing 'my metadata' card");
    }

    public void showModifyMetadataCard() {
        if(currentMainFrameCardType == MainFrameCardType.MODIFY_METADATA) {
            log.info("Already showing 'modify metadata' card");
            return;
        }
        currentMainFrameCardType = MainFrameCardType.MODIFY_METADATA;
        mainFrameCardLayout.show(cardPanel, MainFrameCardType.MODIFY_METADATA.name());
        log.info("Showing 'modify metadata' card");
    }
}
