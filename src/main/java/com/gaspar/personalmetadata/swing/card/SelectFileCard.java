package com.gaspar.personalmetadata.swing.card;

import com.gaspar.personalmetadata.swing.MainFrameView;
import com.gaspar.personalmetadata.swing.ModifyMetadataCardView;
import com.gaspar.personalmetadata.utils.FileDrop;
import com.gaspar.personalmetadata.utils.FileUtils;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

@Lazy
@Slf4j
@Component
public class SelectFileCard extends JPanel {

    private static final int PREVIEW_IMAGE_SIZE = 600;

    private final MainFrameView mainFrameView;

    @Setter(onMethod_ = {@Autowired, @Lazy})
    private ModifyMetadataCardView modifyMetadataCardView;

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

    public SelectFileCard(MainFrameView mainFrameView) {
        this.mainFrameView = mainFrameView;

        add(contentPane);
        new FileDrop(contentPane, this::handleFilesDropped);
        deselectButton.addActionListener(this::deselectDroppedFile);
        findMetadataButton.addActionListener(this::handleFindMetadata);
    }

    private void handleFilesDropped(File[] files) {
        if (files.length == 0) {
            log.info("Not processing empty drop");
            return;
        }
        if (files.length > 1) {
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

        mainFrameView.mainFrameContentsChanged();
    }

    private void previewFileIfPossible(Path file, String contentType) {
        if (contentType.startsWith("image/")) {
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

    public void deselectDroppedFile(ActionEvent e) {
        filePathTextField.setText("");
        fileNameTextField.setText("");
        contentTypeTextField.setText("");
        fileIdTextField.setText("");

        if (previewImageLabel != null) {
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

        mainFrameView.mainFrameContentsChanged();
        log.info("File deselected");
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
        contentPane.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, 20, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setHorizontalAlignment(0);
        label1.setHorizontalTextPosition(0);
        label1.setText("Drag and drop a file here");
        contentPane.add(label1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        previewPanel = new JPanel();
        previewPanel.setLayout(new BorderLayout(0, 0));
        contentPane.add(previewPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(600, 600), new Dimension(600, 600), new Dimension(600, 600), 0, false));
        previewPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        previewLabel = new JLabel();
        previewLabel.setHorizontalAlignment(0);
        previewLabel.setText("Not previewable");
        previewPanel.add(previewLabel, BorderLayout.CENTER);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(6, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("File path:");
        panel1.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        filePathTextField = new JTextField();
        filePathTextField.setEditable(false);
        filePathTextField.setToolTipText("Folder where this file is located");
        panel1.add(filePathTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(300, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("File name:");
        panel1.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fileNameTextField = new JTextField();
        fileNameTextField.setEditable(false);
        fileNameTextField.setToolTipText("File name on your machine");
        panel1.add(fileNameTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(300, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Content type:");
        panel1.add(label4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        contentTypeTextField = new JTextField();
        contentTypeTextField.setEditable(false);
        contentTypeTextField.setToolTipText("MIME content type of the file");
        panel1.add(contentTypeTextField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(300, -1), null, 0, false));
        findMetadataButton = new JButton();
        findMetadataButton.setEnabled(false);
        findMetadataButton.setText("Find metadata for this file");
        findMetadataButton.setToolTipText("Query the database for info on this file");
        panel1.add(findMetadataButton, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deselectButton = new JButton();
        deselectButton.setEnabled(false);
        deselectButton.setText("Deselect");
        deselectButton.setToolTipText("Allows to select a new file");
        panel1.add(deselectButton, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("File ID");
        panel1.add(label5, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fileIdTextField = new JTextField();
        fileIdTextField.setEditable(false);
        fileIdTextField.setToolTipText("Internal ID for this file");
        panel1.add(fileIdTextField, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
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
