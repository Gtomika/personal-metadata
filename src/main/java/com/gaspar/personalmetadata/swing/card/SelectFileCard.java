package com.gaspar.personalmetadata.swing.card;

import com.gaspar.personalmetadata.swing.frame.MainFrame;
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

@Lazy
@Slf4j
@Component
public class SelectFileCard extends JPanel {

    private static final int PREVIEW_IMAGE_SIZE = 600;

    private MainFrame mainFrame;
    private JPanel contentPane;

    private JPanel previewPanel;
    private JLabel previewLabel;
    private JTextField filePathTextField;
    private JTextField fileNameTextField;
    private JTextField contentTypeTextField;
    private JButton findMetadataButton;
    private JButton deselectButton;
    private JTextField fileIdTextField;

    private JLabel imageLabel;

    public SelectFileCard() {
        add(contentPane);
        new FileDrop(contentPane, this::handleFilesDropped);
        deselectButton.addActionListener(this::deselectDroppedFile);
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    private void handleFilesDropped(File[] files) {
        if(files.length == 0) {
            log.info("Not processing empty drop");
            return;
        }
        if(files.length > 1) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Multiple files dropped, only the first one will be used",
                    "Multiple files",
                    JOptionPane.WARNING_MESSAGE
            );
        }
        Path selectedFile = files[0].toPath();
        log.info("The file '{}' was selected for displaying", selectedFile);
        displayDroppedFile(selectedFile);
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

        mainFrame.pack();
    }

    private void previewFileIfPossible(Path file, String contentType) {
        if(contentType.startsWith("image/")) {
            log.info("File '{}' has content type '{}', which makes it previewable", file, contentType);
            previewLabel.setVisible(false);
            previewLabel.repaint();

            try {
                BufferedImage bufferedImage = ImageIO.read(file.toFile());
                Image resizedImage = FileUtils.resizeImage(bufferedImage, PREVIEW_IMAGE_SIZE);
                imageLabel = new JLabel(new ImageIcon(resizedImage));
                previewPanel.add(imageLabel, BorderLayout.CENTER);
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

        if(imageLabel != null) {
            log.info("Image cleared from preview");
            previewPanel.remove(imageLabel);
            imageLabel = null;
        }
        previewLabel.setVisible(true);
        previewLabel.repaint();

        deselectButton.setEnabled(false);
        findMetadataButton.setEnabled(false);

        mainFrame.pack();
        log.info("File deselected");
    }
}
