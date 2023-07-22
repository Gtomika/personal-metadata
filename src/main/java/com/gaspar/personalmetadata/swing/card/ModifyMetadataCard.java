package com.gaspar.personalmetadata.swing.card;

import com.gaspar.personalmetadata.config.LoggedInUserConfig;
import com.gaspar.personalmetadata.config.RecommendedMetadataConfig;
import com.gaspar.personalmetadata.repo.Metadata;
import com.gaspar.personalmetadata.repo.MetadataHead;
import com.gaspar.personalmetadata.repo.MetadataRepository;
import com.gaspar.personalmetadata.swing.LoadingPanelView;
import com.gaspar.personalmetadata.swing.MainFrameView;
import com.gaspar.personalmetadata.swing.SelectFileCardView;
import com.gaspar.personalmetadata.task.DeleteMetadataTask;
import com.gaspar.personalmetadata.task.GetMetadataTask;
import com.gaspar.personalmetadata.task.SaveMetadataTask;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Lazy
@Slf4j
@Component
public class ModifyMetadataCard extends JPanel {

    private final LoggedInUserConfig loggedInUserConfig;
    private final MainFrameView mainFrameView;
    private final LoadingPanelView loadingPanelView;
    @Setter(onMethod_ = {@Autowired, @Lazy})
    private SelectFileCardView selectFileCardView;
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
    private JTextField metadataStateField;
    private JPanel scrollPaneHolder;

    private String fileId;
    private JLabel previewImageLabel;
    private JPanel metadataHolderPanel;
    private MetadataState metadataState;

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
        initializeMetadataHolder();
        metadataOpt.ifPresentOrElse(metadata -> {
            metadataState = MetadataState.FOUND;
            metadataStateField.setText(MetadataState.FOUND.getText());
            deleteButton.setEnabled(true);
            displayMetadataAttributes(metadata.attributes());
        }, () -> {
            metadataState = MetadataState.NOT_FOUND;
            metadataStateField.setText(MetadataState.NOT_FOUND.getText());
            displayDefaultMetadataAttributes();
        });
        loadingPanelView.hideLoading();
        mainFrameView.mainFrameContentsChanged();
    }

    private void processMetadataFailedToFetch(Exception e) {
        log.error("Failed to fetch metadata from DynamoDB for user ID '{}' and file ID '{}'", loggedInUserConfig.getUserId(), fileId, e);
        metadataState = MetadataState.ERROR;
        metadataStateField.setText(MetadataState.ERROR.getText());

        clearMetadataAttributes();
        initializeMetadataHolder();
        displayDefaultMetadataAttributes();

        loadingPanelView.hideLoading();
        mainFrameView.mainFrameContentsChanged();
    }

    private void initializeMetadataHolder() {
        metadataHolderPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(metadataHolderPanel, BoxLayout.Y_AXIS);
        metadataHolderPanel.setLayout(boxLayout);

        JScrollPane scrollPane = new JScrollPane(metadataHolderPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPaneHolder.add(scrollPane);
    }

    private void displayDefaultMetadataAttributes() {
        for(String recommendedAttributeName: recommendedMetadataConfig.getRecommendedConfig()) {
            MetadataAttributePanel attributePanel = MetadataAttributePanel.ofEmptyValue(recommendedAttributeName);
            metadataHolderPanel.add(attributePanel);
        }
    }

    private void displayMetadataAttributes(Map<String, String> attributes) {
        for(var attribute: attributes.entrySet()) {
            MetadataAttributePanel attributePanel = MetadataAttributePanel.of(attribute.getKey(), attribute.getValue());
            metadataHolderPanel.add(attributePanel);
        }
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
            deleteButton.setEnabled(false);
            mainFrameView.showSelectFileCard();
        } else {
            log.info("User aborted return to file selection");
        }
    }

    private void handleDelete(ActionEvent event) {
        int choice = JOptionPane.showConfirmDialog(
                mainFrameView.getDialogParent(),
                "Are you sure you want to delete all metadata associated with this file?",
                "Delete confirmation",
                JOptionPane.YES_NO_OPTION
        );
        if(choice == JOptionPane.YES_OPTION) {
            log.info("User confirmed deletion of all metadata related to the file");
            loadingPanelView.showLoading();
            DeleteMetadataTask task = new DeleteMetadataTask(
                    this::handleDeleteSuccessful,
                    this::handleDeleteFailed,
                    loggedInUserConfig.getUserId(),
                    fileId,
                    metadataRepository
            );
            task.execute();
        } else {
            log.info("User has cancelled the deletion of all metadata related to the file");
        }
    }

    private void handleDeleteSuccessful(Void v) {
        log.info("Delete of metadata finished, user ID '{}', file ID '{}'", loggedInUserConfig.getUserId(), fileId);
        //redirect to the file selection panel, user deleted this file
        selectFileCardView.resetSelectFileCard();
        mainFrameView.showSelectFileCard();
        mainFrameView.mainFrameContentsChanged();
        loadingPanelView.hideLoading();
    }

    private void handleDeleteFailed(Exception e) {
        log.error("Delete of metadata failed, user ID '{}', file ID '{}'", loggedInUserConfig.getUserId(), fileId, e);
        metadataState = MetadataState.ERROR;
        metadataStateField.setText(MetadataState.ERROR.getText());
        loadingPanelView.hideLoading();
        JOptionPane.showMessageDialog(
                mainFrameView.getDialogParent(),
                "Sorry, the deletion has failed: " + e.getClass().getName() + " | " + e.getMessage(),
                "Delete error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void handleNewMetadataAttribute(ActionEvent event) {
        if(metadataHolderPanel != null) {
            metadataHolderPanel.add(MetadataAttributePanel.ofEmpty());
            mainFrameView.mainFrameContentsChanged();
            log.info("New empty metadata attribute has been added");
        } else {
            log.error("Metadata holder was null, so cannot add new metadata attribute to it");
        }
    }

    private void handleSave(ActionEvent event) {
        if(metadataHolderPanel == null) {
            log.error("Metadata holder was null, so cannot save metadata");
            return;
        }
        try {
            Metadata metadata = collectMetadataFromAttributes();
            loadingPanelView.showLoading();
            SaveMetadataTask task = new SaveMetadataTask(this::handleSaveSuccessful, this::handleSaveFailed, metadata, metadataRepository);
            task.execute();
        } catch (MetadataInvalidException e) {
            log.warn("Metadata in invalid state, cannot proceed with save");
            JOptionPane.showMessageDialog(
                    mainFrameView.getDialogParent(),
                    "Some attributes names are empty! Please fill all attribute names! Please fill at least 1 metadata value!",
                    "Invalid attributes",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleSaveSuccessful(Void v) {
        log.info("Metadata saved to DynamoDB for user ID '{}' and file ID '{}'", loggedInUserConfig.getUserId(), fileId);
        selectFileCardView.resetSelectFileCard();
        mainFrameView.showSelectFileCard();
        mainFrameView.mainFrameContentsChanged();
        loadingPanelView.hideLoading();
    }

    private void handleSaveFailed(Exception e) {
        log.error("Failed to save metadata to DynamoDB for user ID '{}' and file ID '{}'", loggedInUserConfig.getUserId(), fileId, e);
        metadataState = MetadataState.ERROR;
        metadataStateField.setText(MetadataState.ERROR.getText());
        loadingPanelView.hideLoading();
        JOptionPane.showMessageDialog(
                mainFrameView.getDialogParent(),
                "Sorry, the save has failed: " + e.getClass().getName() + " | " + e.getMessage(),
                "Save error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private Metadata collectMetadataFromAttributes() throws MetadataInvalidException {
        if(metadataHolderPanel == null) {
            log.error("Cannot collect metadata because holder is not initialized!");
            throw new MetadataInvalidException("Holder not initialized");
        };
        MetadataHead metadataHead = new MetadataHead(
                loggedInUserConfig.getUserId(),
                fileId,
                filePathField.getText(),
                String.valueOf(System.currentTimeMillis())
        );
        Map<String, String> metadataAttributes = new HashMap<>();
        for(java.awt.Component component: metadataHolderPanel.getComponents()) {
            if(component instanceof MetadataAttributePanel attributePanel) {
                if(attributePanel.validState()) {
                    if(!attributePanel.attributeValue().isBlank()) {
                        metadataAttributes.put(attributePanel.attributeName(), attributePanel.attributeValue());
                    } else {
                        log.info("Empty attribute '{}' will not be saved", attributePanel.attributeName());
                    }
                } else {
                    throw new MetadataInvalidException("Attribute is invalid, name not provided");
                }
            } else {
                log.error("Unexpected component with type '{}' in metadata holder panel", component.getClass().getName());
            }
        }
        if(metadataAttributes.isEmpty()) {
            throw new MetadataInvalidException("No attributes specified");
        }
        return new Metadata(metadataHead, metadataAttributes);
    }


}
