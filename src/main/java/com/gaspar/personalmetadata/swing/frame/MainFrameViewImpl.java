package com.gaspar.personalmetadata.swing.frame;

import com.gaspar.personalmetadata.swing.MainFrameView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MainFrameViewImpl implements MainFrameView {

    private final ApplicationContext applicationContext;
    private MainFrame mainFrame;

    @Override
    public void showMainFrame() {
        mainFrame = applicationContext.getBean(MainFrame.class);
        mainFrame.setVisible(true);
        mainFrame.toFront();
    }

    @Override
    public void showSelectFileCard() {
        mainFrame.showSelectFileCard();
    }

    @Override
    public void showModifyMetadataCard() {
        mainFrame.showModifyMetadataCard();
    }

    @Override
    public void packMainFrame() {
        mainFrame.pack();
    }

    @Override
    public JFrame getDialogParent() {
        return mainFrame;
    }
}
