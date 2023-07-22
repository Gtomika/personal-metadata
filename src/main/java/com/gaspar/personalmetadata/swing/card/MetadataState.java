package com.gaspar.personalmetadata.swing.card;

import lombok.Getter;

public enum MetadataState {
    NOT_FOUND("Not found"),
    FOUND("Found"),
    SAVED("Saved"),
    ERROR("Error");

    @Getter
    private final String text;

    MetadataState(String text) {
        this.text = text;
    }
}
