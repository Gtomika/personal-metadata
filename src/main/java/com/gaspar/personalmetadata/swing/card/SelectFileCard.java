package com.gaspar.personalmetadata.swing.card;

import com.gaspar.personalmetadata.swing.MainFrameView;
import com.gaspar.personalmetadata.swing.ModifyMetadataCardView;
import com.gaspar.personalmetadata.utils.FileDrop;
import com.gaspar.personalmetadata.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

@Lazy
@Slf4j
@Component
public class SelectFileCard extends JPanel {

    private static final int PREVIEW_IMAGE_SIZE = 600;

    private final MainFrameView mainFrameView;
    private final ModifyMetadataCardView modifyMetadataCardView;

    private JPanel contentPane;
    private JPanel previewPanel;
    private JLabel previewLabel;
    private JTextField filePathTextField;
    private JTextField fileNameTextField;
    private JTextField contentTypeTextField;
    private JButton findMetadataButton;
    private JButton deselectButton;
    private JTextField fileIdTextField;

    private Path selectedFile;
    private JLabel previewImageLabel;
    private Image resizedImage;

    public SelectFileCard(MainFrameView mainFrameView, ModifyMetadataCardView modifyMetadataCardView) {
        this.mainFrameView = mainFrameView;
        this.modifyMetadataCardView = modifyMetadataCardView;

        add(contentPane);
        new FileDrop(contentPane, this::handleFilesDropped);
        deselectButton.addActionListener(this::deselectDroppedFile);
        findMetadataButton.addActionListener(this::handleFindMetadata);
    }

    private void handleFilesDropped(File[] files) {
        if(files.length == 0) {
            log.info("Not processing empty drop");
            return;
        }
        if(files.length > 1) {
            JOptionPane.showMessageDialog(
                    mainFrameView.getDialogParent(),
                    "Multiple files dropped, only the first one will be used",
                    "Multiple files",
                    JOptionPane.WARNING_MESSAGE
            );
        }
        selectedFile = files[0].toPath();
        log.info("The file '{}' was selected for displaying", selectedFile);
        displayDroppedFile(selectedFile);
    }

    private void handleFindMetadata(ActionEvent event) {
        //button is only enabled when data is selected
        log.info("Switching to modify metadata, with file '{}'", selectedFile);
        modifyMetadataCardView.prepareModifyMetadataCard(selectedFile, fileIdTextField.getText(), Optional.ofNullable(resizedImage));
        mainFrameView.showModifyMetadataCard();
    }

    private void displayDroppedFile(Path file) {
        filePathTextField.setText(FileUtils.extractPathToFile(file));
        fileNameTextField.setText(FileUtils.extractFileName(file));

        String contentType = FileUtils.getContentType(file);
        contentTypeTextField.setText(contentType);

        String fileId = FileUtils.fileHash(file);
        fileIdTextField.setText(fileId);

        previewFileIfPossible(file, contentType);

        deselectButton.setEnabled(true);
        findMetadataButton.setEnabled(true);

        mainFrameView.packMainFrame();
    }

    private void previewFileIfPossible(Path file, String contentType) {
        if(contentType.startsWith("image/")) {
            log.info("File '{}' has content type '{}', which makes it previewable", file, contentType);
            previewLabel.setVisible(false);
            previewLabel.repaint();

            try {
                BufferedImage bufferedImage = ImageIO.read(file.toFile());
                resizedImage = FileUtils.resizeImage(bufferedImage, PREVIEW_IMAGE_SIZE);
                previewImageLabel = new JLabel(new ImageIcon(resizedImage));
                previewPanel.add(previewImageLabel, BorderLayout.CENTER);
            } catch (Exception e) {
                log.error("Failed to preview image '{}'", file, e);
                previewLabel.setVisible(true);
                previewLabel.repaint();
            }
        } else {
            log.info("File '{}' has content type '{}', which is not previewable", file, contentType);
            previewLabel.setVisible(true);
            previewLabel.repaint();
        }
    }

    private void deselectDroppedFile(ActionEvent e) {
        filePathTextField.setText("");
        fileNameTextField.setText("");
        contentTypeTextField.setText("");
        fileIdTextField.setText("");

        if(previewImageLabel != null) {
            log.info("Image cleared from preview");
            previewPanel.remove(previewImageLabel);
            previewImageLabel = null;
        }
        previewLabel.setVisible(true);
        previewLabel.repaint();

        deselectButton.setEnabled(false);
        findMetadataButton.setEnabled(false);

        selectedFile = null;
        resizedImage = null;

        mainFrameView.packMainFrame();
        log.info("File deselected");
    }
}
