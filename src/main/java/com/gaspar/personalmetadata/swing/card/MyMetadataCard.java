package com.gaspar.personalmetadata.swing.card;

import com.gaspar.personalmetadata.repo.MetadataHead;
import com.gaspar.personalmetadata.utils.DateUtils;
import com.gaspar.personalmetadata.utils.FileUtils;
import com.gaspar.personalmetadata.config.LoggedInUserConfig;
import com.gaspar.personalmetadata.repo.MetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Lazy
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MyMetadataCard extends JPanel {

    private static final String[] COLUMN_NAMES = new String[] {"ID", "Last known path", "Filename", "Created at"};

    private final MetadataRepository metadataRepository;
    private final LoggedInUserConfig loggedInUserConfig;

    private JPanel contentPane;
    private JScrollPane metadataScrollPane;
    private JProgressBar progressBar;

    public MyMetadataCard(
            MetadataRepository metadataRepository,
            LoggedInUserConfig loggedInUserConfig
    ) {
        add(contentPane);
        this.metadataRepository = metadataRepository;
        this.loggedInUserConfig = loggedInUserConfig;
    }
}
