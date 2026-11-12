package com.mobile.billing.domain.ports.out;

import java.io.InputStream;

public interface StoragePort {
    String uploadTemporaryFile(InputStream inputStream, String fileName);

    InputStream downloadFile(String storageUri);

    void deleteFile(String storageUri);
}
