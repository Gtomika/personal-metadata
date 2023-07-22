package com.gaspar.personalmetadata.task;

import com.gaspar.personalmetadata.repo.Metadata;
import com.gaspar.personalmetadata.repo.MetadataRepository;

import java.util.function.Consumer;

public class SaveMetadataTask extends AbstractMetadataTask<Void, Object> {

    private final Metadata metadata;
    private final MetadataRepository metadataRepository;

    public SaveMetadataTask(
            Consumer<Void> onSuccess,
            Consumer<Exception> onFail,
            Metadata metadata,
            MetadataRepository metadataRepository
    ) {
        super(onSuccess, onFail);
        this.metadata = metadata;
        this.metadataRepository = metadataRepository;
    }

    @Override
    protected Void doInBackground() throws Exception {
        metadataRepository.putMetadata(metadata);
        return null;
    }
}
