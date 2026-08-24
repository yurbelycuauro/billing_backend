package com.mobile.billing.infrastructure.adapter.in.rest;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/consumption")
@AllArgsConstructor
public class ProcessConsumptionController {

    private final ProcessConsumptionUseCase processConsumptionUseCase;
    private final GetConsumptionDetailUseCase getConsumptionDetailUseCase;

    @PostMapping(path = "/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> processExcelConsumption(
            @RequestParam Integer anioPeriodo,
            @RequestParam Integer mesPeriodo,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo Excel es obligatorio para el procesamiento por Excel.");
        }

        var command = new ProcessConsumptionUseCase.ProcessConsumptionCommand(
            DataSource.MANUAL_EXCEL,
            anioPeriodo,
            mesPeriodo,
            file.getBytes()
        );

        processConsumptionUseCase.processConsumption(
            command
        );

        return ResponseEntity.ok("Consumo procesado correctamente desde Excel.");
    }

    @PostMapping(path = "/database")
    public ResponseEntity<String> processDatabaseConsumption(
            @RequestParam Integer anioPeriodo,
            @RequestParam Integer mesPeriodo
    ) {
        processConsumptionUseCase.processConsumption(
            new ProcessConsumptionUseCase.ProcessConsumptionCommand(
                DataSource.DIRECT_DB,
                anioPeriodo,
                mesPeriodo,
                null
            )
        );

        return ResponseEntity.ok("Consumo procesado correctamente desde la base de datos.");
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

        List<ConsumptionDetailSummary> consumptionDetailResponse = getConsumptionDetailUseCase.getSummaryConsumptionDetail(command);

        return ResponseEntity.ok(consumptionDetailResponse); // Retorna HTTP 200 OK
    }
}
