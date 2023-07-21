package com.gaspar.personalmetadata.swing;

import javax.swing.*;

public interface LoadingPanelView {

    void showLoading();

    void hideLoading();

    void attachLoadingPanel(JPanel parent, Object attribute);

}
