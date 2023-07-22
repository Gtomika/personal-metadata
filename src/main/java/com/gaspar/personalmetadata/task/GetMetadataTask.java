package com.gaspar.personalmetadata.task;

import com.gaspar.personalmetadata.repo.Metadata;
import com.gaspar.personalmetadata.repo.MetadataRepository;

import java.util.Optional;
import java.util.function.Consumer;

public class GetMetadataTask extends AbstractMetadataTask<Optional<Metadata>, Object> {

    private final String userId;
    private final String fileId;
    private final MetadataRepository metadataRepository;

    public GetMetadataTask(
            Consumer<Optional<Metadata>> onSuccess,
            Consumer<Exception> onFail,
            String userId,
            String fileId,
            MetadataRepository metadataRepository
    ) {
        super(onSuccess, onFail);
        this.userId = userId;
        this.fileId = fileId;
        this.metadataRepository = metadataRepository;
    }

    @Override
    protected Optional<Metadata> doInBackground() {
        return metadataRepository.getMetadata(userId, fileId);
    }

}
