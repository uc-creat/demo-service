package com.tw.prograd.image.storage.exception;

import java.io.IOException;

public class StorageInitializeException extends RuntimeException {
    public StorageInitializeException(String message) {
        super(message);
    }

    public StorageInitializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
