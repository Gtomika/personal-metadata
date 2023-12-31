package com.gaspar.personalmetadata.task;

import com.gaspar.personalmetadata.repo.Metadata;
import com.gaspar.personalmetadata.repo.MetadataRepository;

import java.util.Optional;
import java.util.function.Consumer;

public class GetMetadataTask extends AbstractMetadataTask<Optional<Metadata>, Object> {

    public GetMetadataTask(
            Consumer<Optional<Metadata>> onSuccess,
            Consumer<Exception> onFail,
            String userId,
            String fileId,
            MetadataRepository metadataRepository
    ) {
        super(onSuccess, onFail, userId, fileId, metadataRepository);
    }

    @Override
    protected Optional<Metadata> doInBackground() {
        return metadataRepository.getMetadata(userId, fileId);
    }

}
