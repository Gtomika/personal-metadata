package com.gaspar.personalmetadata.swing.card;

import com.gaspar.personalmetadata.config.LoggedInUserConfig;
import com.gaspar.personalmetadata.swing.MainFrameView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.util.Optional;

@Lazy
@Slf4j
@Component
public class ModifyMetadataCard extends JPanel {

    private final LoggedInUserConfig loggedInUserConfig;
    private final MainFrameView mainFrameView;

    private JPanel contentPane;
    private JPanel previewPanel;
    private JLabel previewLabel;
    private JButton cancelButton;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton newMetadataButton;
    private JLabel filePathField;

    private String fileId;
    private JLabel previewImageLabel;

    public ModifyMetadataCard(
            LoggedInUserConfig loggedInUserConfig,
            MainFrameView mainFrameView
    ) {
        this.loggedInUserConfig = loggedInUserConfig;
        this.mainFrameView = mainFrameView;

        add(contentPane);
        cancelButton.addActionListener(this::handleCancel);
        deleteButton.addActionListener(this::handleDelete);
        newMetadataButton.addActionListener(this::handleNewMetadata);
        saveButton.addActionListener(this::handleSave);
    }

    public void prepare(Path file, String fileId, Optional<Image> previewImageOptional) {
        filePathField.setText(file.toString());
        this.fileId = fileId;

        previewImageOptional.ifPresentOrElse(previewImage -> {
            previewImageLabel = new JLabel(new ImageIcon(previewImage));
            previewPanel.add(previewImageLabel, BorderLayout.CENTER);
            previewLabel.setVisible(false);
        }, () -> {
            previewLabel.setVisible(true);
        });
    }

    private void handleCancel(ActionEvent event) {
        int choice = JOptionPane.showConfirmDialog(
                mainFrameView.getDialogParent(),
                "Go back to file selection? Unsaved changes are lost!",
                "Cancel",
                JOptionPane.YES_NO_OPTION
        );
        if(choice == JOptionPane.YES_OPTION) {
            log.info("User confirmed return to file selection");
            mainFrameView.showSelectFileCard();
        } else {
            log.info("User aborted return to file selection");
        }
    }

    private void handleDelete(ActionEvent event) {
        //TODO
    }

    private void handleNewMetadata(ActionEvent event) {
        //TODO
    }

    private void handleSave(ActionEvent event) {
        //TODO
    }


}
