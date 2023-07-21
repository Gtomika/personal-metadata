package com.gaspar.personalmetadata.swing.frame;

import com.gaspar.personalmetadata.MainFrameView;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainFrameViewImpl implements MainFrameView {

    private final ApplicationContext applicationContext;

    @Override
    public void showMainFrame() {
        MainFrame mainFrame = applicationContext.getBean(MainFrame.class);
        mainFrame.setVisible(true);
        mainFrame.toFront();
    }
}
