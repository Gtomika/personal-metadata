package com.gaspar.personalmetadata.swing.card;

import com.gaspar.personalmetadata.config.LoggedInUserConfig;
import com.gaspar.personalmetadata.config.RecommendedMetadataConfig;
import com.gaspar.personalmetadata.repo.Metadata;
import com.gaspar.personalmetadata.repo.MetadataRepository;
import com.gaspar.personalmetadata.swing.LoadingPanelView;
import com.gaspar.personalmetadata.swing.MainFrameView;
import com.gaspar.personalmetadata.task.GetMetadataTask;
import com.gaspar.personalmetadata.task.SaveMetadataTask;
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
    private final LoadingPanelView loadingPanelView;
    private final MetadataRepository metadataRepository;
    private final RecommendedMetadataConfig recommendedMetadataConfig;

    private JPanel contentPane;
    private JPanel previewPanel;
    private JLabel previewLabel;
    private JButton cancelButton;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton newMetadataButton;
    private JLabel filePathField;
    private JTextField fileInDatabaseStatusField;
    private JPanel scrollPaneHolder;

    private String fileId;
    private JLabel previewImageLabel;
    private JPanel metadataHolderPanel;

    public ModifyMetadataCard(
            LoggedInUserConfig loggedInUserConfig,
            MainFrameView mainFrameView,
            LoadingPanelView loadingPanelView,
            MetadataRepository metadataRepository,
            RecommendedMetadataConfig recommendedMetadataConfig
    ) {
        this.loggedInUserConfig = loggedInUserConfig;
        this.mainFrameView = mainFrameView;
        this.loadingPanelView = loadingPanelView;
        this.metadataRepository = metadataRepository;
        this.recommendedMetadataConfig = recommendedMetadataConfig;

        add(contentPane);
        cancelButton.addActionListener(this::handleCancel);
        deleteButton.addActionListener(this::handleDelete);
        newMetadataButton.addActionListener(this::handleNewMetadataAttribute);
        saveButton.addActionListener(this::handleSave);
    }

    public void prepare(Path file, String fileId, Optional<Image> previewImageOptional) {
        loadingPanelView.showLoading(); //because dynamodb get request

        filePathField.setText(file.toString());
        this.fileId = fileId;

        previewImageOptional.ifPresentOrElse(previewImage -> {
            previewImageLabel = new JLabel(new ImageIcon(previewImage));
            previewPanel.add(previewImageLabel, BorderLayout.CENTER);
            previewLabel.setVisible(false);
        }, () -> {
            previewLabel.setVisible(true);
        });

        GetMetadataTask task = new GetMetadataTask(
                this::processMetadata,
                this::processMetadataFailedToFetch,
                loggedInUserConfig.getUserId(),
                fileId,
                metadataRepository
        );
        task.execute();
    }

    private void processMetadata(Optional<Metadata> metadataOpt) {
        clearMetadataAttributes();
        metadataOpt.ifPresentOrElse(metadata -> {
            fileInDatabaseStatusField.setText("Found");
            //TODO
        }, () -> {
            fileInDatabaseStatusField.setText("Not found");
            displayDefaultMetadataAttributes();
        });
        loadingPanelView.hideLoading();
        mainFrameView.packMainFrame();
    }

    private void processMetadataFailedToFetch(Exception e) {
        log.error("Failed to fetch metadata from DynamoDB for user ID '{}' and file ID '{}'", loggedInUserConfig.getUserId(), fileId, e);
        fileInDatabaseStatusField.setText("Error");

        clearMetadataAttributes();
        displayDefaultMetadataAttributes();

        loadingPanelView.hideLoading();
        mainFrameView.packMainFrame();
    }

    private void displayDefaultMetadataAttributes() {
        metadataHolderPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(metadataHolderPanel, BoxLayout.Y_AXIS);
        metadataHolderPanel.setLayout(boxLayout);

        for(String recommendedAttributeName: recommendedMetadataConfig.getRecommendedConfig()) {
            MetadataAttributePanel attributePanel = MetadataAttributePanel.ofEmptyValue(recommendedAttributeName);
            metadataHolderPanel.add(attributePanel);
        }

        JScrollPane scrollPane = new JScrollPane(metadataHolderPanel);
        scrollPaneHolder.add(scrollPane, BorderLayout.CENTER);
    }

    private void clearMetadataAttributes() {
        if(metadataHolderPanel != null) metadataHolderPanel.removeAll();
        scrollPaneHolder.removeAll();
        metadataHolderPanel = null;
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

    private void handleNewMetadataAttribute(ActionEvent event) {
        if(metadataHolderPanel != null) {
            metadataHolderPanel.add(MetadataAttributePanel.ofEmpty());
            metadataHolderPanel.revalidate();
            metadataHolderPanel.repaint();
            log.info("New empty metadata attribute has been added");
        } else {
            log.error("Metadata holder was null, so cannot add new metadata attribute to it");
        }
    }

    private void handleSave(ActionEvent event) {
        if(metadataHolderPanel != null) {
            try {
                Metadata metadata = collectMetadataFromAttributes();
                loadingPanelView.showLoading();
                SaveMetadataTask task = new SaveMetadataTask(v -> {
                    log.info("Metadata saved to DynamoDB for user ID '{}' and file ID '{}'", loggedInUserConfig.getUserId(), fileId);
                    fileInDatabaseStatusField.setText("Saved");
                    loadingPanelView.hideLoading();
                }, e -> {
                    log.error("Failed to save metadata to DynamoDB for user ID '{}' and file ID '{}'", loggedInUserConfig.getUserId(), fileId, e);
                    fileInDatabaseStatusField.setText("Save failed");
                    loadingPanelView.hideLoading();
                }, metadata, metadataRepository);
                task.execute();
            } catch (MetadataInvalidException e) {
                log.warn("Metadata in invalid state, cannot proceed with save");
                JOptionPane.showMessageDialog(
                        mainFrameView.getDialogParent(),
                        "Some attributes names are empty! Please fill all attribute names!",
                        "Invalid attributes",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            log.error("Metadata holder was null, so cannot save metadata");
        }
    }

    private Metadata collectMetadataFromAttributes() throws MetadataInvalidException {
        //TODO use metadata holder
        return null;
    }


}
