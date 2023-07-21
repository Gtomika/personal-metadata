package com.gaspar.personalmetadata.swing;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.Optional;

public interface ModifyMetadataCardView {

    void attachModifyMetadataCard(JPanel parent, Object attribute);

    void prepareModifyMetadataCard(Path file, String fileId, Optional<Image> previewImage);

}
