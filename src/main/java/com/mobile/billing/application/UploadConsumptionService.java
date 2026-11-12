package com.mobile.billing.application;

import java.io.InputStream;

import com.mobile.billing.domain.model.Consumption;
import com.mobile.billing.domain.ports.in.UploadConsumptionUseCase;

public class UploadConsumptionService implements UploadConsumptionUseCase {

    @Override
    public Consumption uploadAndProcessExcel(InputStream fileStream, String fileName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'uploadAndProcessExcel'");
    }

}
