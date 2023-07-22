package com.gaspar.personalmetadata.task;

import com.gaspar.personalmetadata.repo.MetadataRepository;

import java.util.function.Consumer;

public class DeleteMetadataTask extends AbstractMetadataTask<Void, Object> {

    public DeleteMetadataTask(
            Consumer<Void> onSuccess,
            Consumer<Exception> onFail,
            String userId,
            String fileId,
            MetadataRepository metadataRepository
    ) {
        super(onSuccess, onFail, userId, fileId, metadataRepository);
    }

    @Override
    protected Void doInBackground() throws Exception {
        metadataRepository.deleteMetadata(userId, fileId);
        return null;
    }
}
