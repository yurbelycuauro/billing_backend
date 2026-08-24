package com.mobile.billing.application;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.mobile.billing.domain.ports.in.InvoicePdfExporterUseCase;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.InvoiceResponse;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class generatePDFService implements InvoicePdfExporterUseCase {

    private final TemplateEngine templateEngine;

    public generatePDFService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public byte[] generateInvoicePdf(InvoiceResponse invoice) {
        // 1. Cargar datos al contexto de Thymeleaf
        Context context = new Context();
        context.setVariable("invoice", invoice);

        // 2. Renderizar HTML dinámico
        String renderedHtml = templateEngine.process("pdf/invoice-template", context);

        // 3. Convertir HTML a PDF en memoria
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(renderedHtml, "/");
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF de la factura", e);
        }
    }

   

}
