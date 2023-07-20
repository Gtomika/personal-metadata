package com.gaspar.personalmetadata.swing.frame;

import com.gaspar.personalmetadata.config.LoggedInUserConfig;
import com.gaspar.personalmetadata.frame.MainFrameView;
import com.gaspar.personalmetadata.swing.card.ModifyMetadataCard;
import com.gaspar.personalmetadata.swing.card.MyMetadataCard;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainFrameViewImpl implements MainFrameView {

    private final LoggedInUserConfig loggedInUserConfig;
    private final ApplicationContext applicationContext;

    @Override
    public void showMainFrame() {
        //create prototype beans now, after app is initialized
        MyMetadataCard myMetadataCard = applicationContext.getBean(MyMetadataCard.class);
        ModifyMetadataCard modifyMetadataCard = applicationContext.getBean(ModifyMetadataCard.class);

        MainFrame mainFrame = new MainFrame(
                loggedInUserConfig.getUsername(),
                myMetadataCard,
                modifyMetadataCard
        );
        mainFrame.setVisible(true);
    }
}
