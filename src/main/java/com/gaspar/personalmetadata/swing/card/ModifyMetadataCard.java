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
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
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
        for (String recommendedAttributeName : recommendedMetadataConfig.getRecommendedConfig()) {
            MetadataAttributePanel attributePanel = MetadataAttributePanel.ofEmptyValue(recommendedAttributeName);
            metadataHolderPanel.add(attributePanel);
        }
    }

    private void displayMetadataAttributes(Map<String, String> attributes) {
        for (var attribute : attributes.entrySet()) {
            MetadataAttributePanel attributePanel = MetadataAttributePanel.of(attribute.getKey(), attribute.getValue());
            metadataHolderPanel.add(attributePanel);
        }
    }

    private void clearMetadataAttributes() {
        if (metadataHolderPanel != null) metadataHolderPanel.removeAll();
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
        if (choice == JOptionPane.YES_OPTION) {
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
        if (choice == JOptionPane.YES_OPTION) {
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
        if (metadataHolderPanel != null) {
            metadataHolderPanel.add(MetadataAttributePanel.ofEmpty());
            mainFrameView.mainFrameContentsChanged();
            log.info("New empty metadata attribute has been added");
        } else {
            log.error("Metadata holder was null, so cannot add new metadata attribute to it");
        }
    }

    private void handleSave(ActionEvent event) {
        if (metadataHolderPanel == null) {
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
        if (metadataHolderPanel == null) {
            log.error("Cannot collect metadata because holder is not initialized!");
            throw new MetadataInvalidException("Holder not initialized");
        }
        ;
        MetadataHead metadataHead = new MetadataHead(
                loggedInUserConfig.getUserId(),
                fileId,
                filePathField.getText(),
                String.valueOf(System.currentTimeMillis())
        );
        Map<String, String> metadataAttributes = new HashMap<>();
        for (java.awt.Component component : metadataHolderPanel.getComponents()) {
            if (component instanceof MetadataAttributePanel attributePanel) {
                if (attributePanel.validState()) {
                    if (!attributePanel.attributeValue().isBlank()) {
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
        if (metadataAttributes.isEmpty()) {
            throw new MetadataInvalidException("No attributes specified");
        }
        return new Metadata(metadataHead, metadataAttributes);
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, 20, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Metadata editor");
        contentPane.add(label1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        previewPanel = new JPanel();
        previewPanel.setLayout(new BorderLayout(0, 0));
        contentPane.add(previewPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        previewPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        previewLabel = new JLabel();
        previewLabel.setHorizontalAlignment(0);
        previewLabel.setText("Not previewable");
        previewPanel.add(previewLabel, BorderLayout.CENTER);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Go back to file selection");
        panel2.add(cancelButton);
        deleteButton = new JButton();
        deleteButton.setEnabled(false);
        deleteButton.setForeground(new Color(-4521216));
        deleteButton.setText("Delete");
        deleteButton.setToolTipText("Delete all metadata of this file");
        panel2.add(deleteButton);
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1);
        newMetadataButton = new JButton();
        newMetadataButton.setText("New metadata");
        newMetadataButton.setToolTipText("Add a new attribute");
        panel2.add(newMetadataButton);
        saveButton = new JButton();
        saveButton.setText("Save");
        saveButton.setToolTipText("Save metadata and go back to file selection");
        panel2.add(saveButton);
        scrollPaneHolder = new JPanel();
        scrollPaneHolder.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        scrollPaneHolder.setBackground(new Color(-2759958));
        panel1.add(scrollPaneHolder, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(500, 500), new Dimension(500, 500), 0, false));
        filePathField = new JLabel();
        Font filePathFieldFont = this.$$$getFont$$$(null, Font.ITALIC, -1, filePathField.getFont());
        if (filePathFieldFont != null) filePathField.setFont(filePathFieldFont);
        filePathField.setText("[path]");
        filePathField.setToolTipText("Where this file is on your machine");
        contentPane.add(filePathField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        contentPane.add(panel3, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(-1, 30), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("File metadata in database:");
        panel3.add(label2);
        metadataStateField = new JTextField();
        metadataStateField.setEditable(false);
        panel3.add(metadataStateField);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
