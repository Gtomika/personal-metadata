package com.gaspar.personalmetadata.swing;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.Optional;

public interface MainFrameView {

    void showMainFrame();

    void showSelectFileCard();

    void showModifyMetadataCard();

    void packMainFrame();

    JFrame getDialogParent();

}
