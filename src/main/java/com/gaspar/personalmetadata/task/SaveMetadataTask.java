package com.gaspar.personalmetadata.task;

import com.gaspar.personalmetadata.repo.Metadata;
import com.gaspar.personalmetadata.repo.MetadataRepository;

import java.util.function.Consumer;

public class SaveMetadataTask extends AbstractMetadataTask<Void, Object> {

    private final Metadata metadata;

    public SaveMetadataTask(
            Consumer<Void> onSuccess,
            Consumer<Exception> onFail,
            Metadata metadata,
            MetadataRepository metadataRepository
    ) {
        super(onSuccess, onFail, metadata.head().userId(), metadata.head().fileId(), metadataRepository);
        this.metadata = metadata;
    }

    @Override
    protected Void doInBackground() {
        metadataRepository.putMetadata(metadata);
        return null;
    }
}
