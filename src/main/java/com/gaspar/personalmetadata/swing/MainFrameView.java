package com.gaspar.personalmetadata.swing;

import javax.swing.JFrame;

public interface MainFrameView {

    void showMainFrame();

    void showSelectFileCard();

    void showModifyMetadataCard();

    JFrame getDialogParent();

    void mainFrameContentsChanged();
}
