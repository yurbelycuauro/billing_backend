package com.mobile.billing.application;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.ports.in.SendInvoiceUseCase;
import com.mobile.billing.domain.ports.out.EmailSenderPort;

@Service
public class SendInvoiceService implements SendInvoiceUseCase {

    private final EmailSenderPort emailSenderPort;

    public SendInvoiceService(EmailSenderPort emailSenderPort) {
        this.emailSenderPort = emailSenderPort;
    }

    @Override
    public void execute(SendInvoiceCommand command) {
        String asunto = "Tu Factura Adjunta";
        String cuerpo = "Estimado cliente,\n\nAdjunto a este correo encontrará su factura en formato PDF.";
        String fileName="factura";
        emailSenderPort.sendEmailWithAttachment(command.email(), cuerpo, asunto, command.pdfContent(), fileName);
        
    }

}
