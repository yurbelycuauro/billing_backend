package com.mobile.billing.infrastructure.adapter.out.adapter.gcp;

import java.io.InputStream;

import org.springframework.stereotype.Component;

import com.mobile.billing.domain.ports.out.StoragePort;

@Component
public class GcpStorageAdapter implements StoragePort {
    @Override
    public String uploadTemporaryFile(InputStream inputStream, String fileName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'uploadTemporaryFile'");
    }

    @Override
    public InputStream downloadFile(String storageUri) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'downloadFile'");
    }

    @Override
    public void deleteFile(String storageUri) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteFile'");
    }

}
