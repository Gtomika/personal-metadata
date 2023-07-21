package com.gaspar.personalmetadata.swing.frame;

import com.gaspar.personalmetadata.swing.LoadingPanelView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class LoadingPanelViewImpl implements LoadingPanelView {

    private final LoadingPanel loadingPanel;

    @Override
    public void showLoading() {
        loadingPanel.showLoading();
    }

    @Override
    public void hideLoading() {
        loadingPanel.hideLoading();
    }

    @Override
    public void attachLoadingPanel(JPanel parent, Object attribute) {
        parent.add(loadingPanel, attribute);
    }
}
