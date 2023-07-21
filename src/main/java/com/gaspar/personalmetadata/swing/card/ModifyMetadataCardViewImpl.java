package com.gaspar.personalmetadata.swing.card;

import com.gaspar.personalmetadata.swing.ModifyMetadataCardView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ModifyMetadataCardViewImpl implements ModifyMetadataCardView {

    private final ModifyMetadataCard modifyMetadataCard;

    @Override
    public void attachModifyMetadataCard(JPanel parent, Object attribute) {
        parent.add(modifyMetadataCard, attribute);
    }

    @Override
    public void prepareModifyMetadataCard(Path file, String fileId, Optional<Image> previewImage) {
        modifyMetadataCard.prepare(file, fileId, previewImage);
    }
}
