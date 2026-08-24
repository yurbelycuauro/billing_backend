package com.mobile.billing.infrastructure.adapter.out.adapter.email;

import com.mobile.billing.domain.ports.out.EmailSenderPort;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;

@Component
public class SendGridEmailAdapter implements EmailSenderPort {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;
    
    @Override
    public void sendEmailWithAttachment(String recipient, String subject, String body, byte[] fileBytes,
            String fileName) {
        Email from = new Email(fromEmail);
        Email to = new Email(recipient);
        Content content = new Content("text/plain", body);

        Mail mail = new Mail(from, subject, to, content);

        Attachments attachments = new Attachments();
        String base64Content = Base64.getEncoder().encodeToString(fileBytes);
        attachments.setContent(base64Content);
        attachments.setType("application/pdf");
        attachments.setFilename(fileName);
        attachments.setDisposition("attachment");

        mail.addAttachments(attachments);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                throw new RuntimeException("Error en SendGrid: " + response.getBody());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al comunicarse con la API de SendGrid", e);
        }
    }

}
