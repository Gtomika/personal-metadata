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
        asyncLoadMetadataIntoTable();
    }

    /**
     * Starts loading the table contents (the metadata from dynamodb)
     * into this table. Happens on a different thread.
     */
    public void asyncLoadMetadataIntoTable() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            log.info("Starting to load user's metadata into the table...");
            List<MetadataHead> metadataHeadList = metadataRepository.queryMetadata(loggedInUserConfig.getUserId());
            Object[][] dataArray = new Object[metadataHeadList.size()][];
            for(int i = 0; i < metadataHeadList.size(); i++) {
                dataArray[i] = metadataHeadToTableRow(metadataHeadList.get(i));
            }
            //update on main thread when finished
            log.info("Finished loading user's metadata, found {} elements", dataArray.length);
            SwingUtilities.invokeLater(() -> displayDataInTable(dataArray));
        });
        executor.shutdown();
    }

    private String[] metadataHeadToTableRow(MetadataHead head) {
        return new String[] {
                head.fileId(),
                FileUtils.extractPathToFile(head.lastKnownPath()),
                FileUtils.extractFileName(head.lastKnownPath()),
                DateUtils.toFormattedDate(head.createdAt())
        };
    }

    private void displayDataInTable(Object[][] dataArray) {
        DefaultTableModel tableModel = new DefaultTableModel(dataArray, COLUMN_NAMES);

        JTable metadataTable = new JTable(tableModel);
        metadataTable.setFillsViewportHeight(true);

        metadataScrollPane.remove(progressBar);
        metadataScrollPane.add(metadataTable);
        metadataScrollPane.invalidate();
        metadataScrollPane.repaint();

        log.info("Displayed {} elements of user's metadata in scrollable table", dataArray.length);
    }
}
