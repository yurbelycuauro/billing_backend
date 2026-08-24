package com.mobile.billing.domain.ports.in;

import com.mobile.billing.infrastructure.adapter.in.rest.dto.InvoiceResponse;

public interface InvoicePdfExporterUseCase {

    byte[] generateInvoicePdf(InvoiceResponse invoice);  
    
    

}
