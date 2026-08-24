package com.mobile.billing.domain.ports.out;

public interface EmailSenderPort {
    void sendEmailWithAttachment(String recipient, String subject, String body, byte[] fileBytes, String fileName);

}
