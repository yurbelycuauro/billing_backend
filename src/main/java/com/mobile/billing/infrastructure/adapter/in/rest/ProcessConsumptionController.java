package com.mobile.billing.infrastructure.adapter.in.rest;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mobile.billing.domain.model.ConsumptionDetail;
import com.mobile.billing.domain.model.ConsumptionDetailSummary;
import com.mobile.billing.domain.model.DataSource;
import com.mobile.billing.domain.ports.in.GetConsumptionDetailUseCase;
import com.mobile.billing.domain.ports.in.ProcessConsumptionUseCase;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.ConsumptionDetailRequest;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/consumption")
@Slf4j
public class ProcessConsumptionController {

    private final ProcessConsumptionUseCase processConsumptionUseCase;
    private final GetConsumptionDetailUseCase getConsumptionDetailUseCase;

    public ProcessConsumptionController(
            ProcessConsumptionUseCase processConsumptionUseCase,
            GetConsumptionDetailUseCase getConsumptionDetailUseCase) {
        this.processConsumptionUseCase = processConsumptionUseCase;
        this.getConsumptionDetailUseCase = getConsumptionDetailUseCase;
    }

    @PostMapping(path = "/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> processExcelConsumption(
            @RequestParam Integer anioPeriodo,
            @RequestParam Integer mesPeriodo,
            @RequestPart("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo Excel es obligatorio para el procesamiento por Excel.");
        }

        var command = new ProcessConsumptionUseCase.ProcessConsumptionCommand(
                DataSource.MANUAL_EXCEL,
                anioPeriodo,
                mesPeriodo,
                file.getBytes());

        return submitProcessing(command, "Procesamiento de consumo desde Excel aceptado.");
    }

    @PostMapping(path = "/database")
    public ResponseEntity<String> processDatabaseConsumption(
            @RequestParam Integer anioPeriodo,
            @RequestParam Integer mesPeriodo) {
        return submitProcessing(
                new ProcessConsumptionUseCase.ProcessConsumptionCommand(
                        DataSource.DIRECT_DB,
                        anioPeriodo,
                        mesPeriodo,
                        null),
                "Procesamiento de consumo desde la base de datos aceptado.");
    }

    private ResponseEntity<String> submitProcessing(
            ProcessConsumptionUseCase.ProcessConsumptionCommand command,
            String acceptedMessage) {
        CompletableFuture<Void> processing = processConsumptionUseCase.processConsumption(command);
        processing.whenComplete((result, error) -> {
            if (error != null) {
                log.error("Falló el procesamiento de consumos para año {} y mes {}.",
                        command.anioPeriodo(), command.mesPeriodo(), error);
            }
        });
        return ResponseEntity.accepted().body(acceptedMessage);
    }

    @GetMapping(value = "/listConsumptionDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ConsumptionDetail>> listConsumptionDetails(
            @Valid @ModelAttribute ConsumptionDetailRequest request) {

        GetConsumptionDetailUseCase.GetConsumptionDetailCommand command = new GetConsumptionDetailUseCase.GetConsumptionDetailCommand(
                request.clientId(),
                request.anioPeriodo(),
                request.mesPeriodo());

        List<ConsumptionDetail> consumptionDetailResponse = getConsumptionDetailUseCase.getConsumptionDetail(command);

        return ResponseEntity.ok(consumptionDetailResponse); // Retorna HTTP 200 OK
    }

    @GetMapping(value = "/listSummaryConsumptionDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ConsumptionDetailSummary>> listSummaryConsumptionDetails(
            @Valid @ModelAttribute ConsumptionDetailRequest request) {

        GetConsumptionDetailUseCase.GetConsumptionDetailCommand command = new GetConsumptionDetailUseCase.GetConsumptionDetailCommand(
                request.clientId(),
                request.anioPeriodo(),
                request.mesPeriodo());

        List<ConsumptionDetailSummary> consumptionDetailResponse = getConsumptionDetailUseCase
                .getSummaryConsumptionDetail(command);

        return ResponseEntity.ok(consumptionDetailResponse); // Retorna HTTP 200 OK
    }

    @PostMapping("/process")
    public ResponseEntity<String> receivePubSubMessage(@RequestBody Map<String, Object> payload) {
        try {
            // Pub/Sub envía los mensajes envueltos en una estructura estandarizada (Push
            // format)
            Map<String, Object> message = (Map<String, Object>) payload.get("message");

            if (message != null && message.containsKey("data")) {
                String encodedData = (String) message.get("data");

                // Los datos viajan codificados en Base64
                byte[] decodedBytes = Base64.getDecoder().decode(encodedData);
                String fileUri = new String(decodedBytes);

                // AQUÍ LLAMAS A TU SERVICIO DE PROCESAMIENTO DE EXCEL
                System.out.println("Archivo recibido para procesar desde GCS: " + fileUri);

                // Procesar por streaming usando Storage...
            }

            // Retornar 200 OK le confirma a Pub/Sub que el mensaje fue recibido con éxito
            return ResponseEntity.ok("Mensaje procesado correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            // Retornar un error 500 hará que Pub/Sub reintente enviar el mensaje más tarde
            return ResponseEntity.internalServerError().body("Error procesando evento");
        }
    }
}
