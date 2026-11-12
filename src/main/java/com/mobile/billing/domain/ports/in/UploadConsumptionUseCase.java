package com.mobile.billing.domain.ports.in;

import java.io.InputStream;

import com.mobile.billing.domain.model.Consumption;

public interface UploadConsumptionUseCase {
    Consumption uploadAndProcessExcel(InputStream fileStream, String fileName);

}
