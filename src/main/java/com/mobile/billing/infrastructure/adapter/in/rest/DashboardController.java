package com.mobile.billing.infrastructure.adapter.in.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mobile.billing.domain.model.DashboardSummary;
import com.mobile.billing.domain.ports.in.GetDashboardUseCase;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.DashboardResponse;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final GetDashboardUseCase getDashboardUseCase;
    private final DashboardMetricsMapper dashboardMetricsMapper;

    public DashboardController(
            GetDashboardUseCase getDashboardUseCase,
            DashboardMetricsMapper dashboardMetricsMapper) {
        this.getDashboardUseCase = getDashboardUseCase;
        this.dashboardMetricsMapper = dashboardMetricsMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DashboardResponse> getDashboard(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        DashboardSummary dashboard = getDashboardUseCase.getDashboard(
                new GetDashboardUseCase.DashboardQuery(year, month));
        return ResponseEntity.ok(dashboardMetricsMapper.toResponse(dashboard));
    }
}