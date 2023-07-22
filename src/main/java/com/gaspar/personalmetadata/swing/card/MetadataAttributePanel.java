package com.gaspar.personalmetadata.swing.card;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@Slf4j
public class MetadataAttributePanel extends JPanel {

    private JPanel contentPane;
    private JTextField nameField;
    private JTextField valueField;
    private JButton deleteButton;

    private MetadataAttributePanel(String name, String value) {
        contentPane.setPreferredSize(new Dimension(-1, 40));
        add(contentPane);
        nameField.setText(name);
        valueField.setText(value);
        deleteButton.addActionListener(this::handleDelete);
    }

    private void handleDelete(ActionEvent event) {
        log.info("User selected to remove metadata attribute '{}'", nameField.getText());
        Container parent = getParent();
        parent.remove(this);
        parent.revalidate();
        parent.repaint();
    }

    private boolean validState() {
        return !nameField.getText().isBlank(); //value field can be empty
    }

    public static MetadataAttributePanel of(String name, String value) {
        return new MetadataAttributePanel(name, value);
    }

    public static MetadataAttributePanel ofEmptyValue(String name) {
        return new MetadataAttributePanel(name, null);
    }

    public static MetadataAttributePanel ofEmpty() {
        return new MetadataAttributePanel(null, null);
    }

}
