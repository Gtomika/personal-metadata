package com.gaspar.personalmetadata.swing.card;

import com.gaspar.personalmetadata.swing.SelectFileCardView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@RequiredArgsConstructor
public class SelectFileCardViewImpl implements SelectFileCardView {

    private final SelectFileCard selectFileCard;

    @Override
    public void attachSelectFileCard(JPanel panel, Object attribute) {
        panel.add(selectFileCard, attribute);
    }

    @Override
    public void resetSelectFileCard() {
        selectFileCard.deselectDroppedFile(null);
    }
}
