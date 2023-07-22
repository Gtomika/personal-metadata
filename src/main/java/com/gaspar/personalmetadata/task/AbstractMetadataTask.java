package com.gaspar.personalmetadata.task;

import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.util.function.Consumer;

@RequiredArgsConstructor
public abstract class AbstractMetadataTask<T, V> extends SwingWorker<T, V> {

    protected final Consumer<T> onSuccess;
    protected final Consumer<Exception> onFail;

    @Override
    protected abstract T doInBackground() throws Exception;

    @Override
    protected void done() {
        try {
            onSuccess.accept(get());
        } catch (Exception e) {
            onFail.accept(e);
        }
    }
}
