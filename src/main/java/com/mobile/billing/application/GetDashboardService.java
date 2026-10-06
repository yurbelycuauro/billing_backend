package com.mobile.billing.application;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.model.DashboardSummary;
import com.mobile.billing.domain.ports.in.GetDashboardUseCase;
import com.mobile.billing.domain.ports.out.DashboardRepositoryPort;

@Service
public class GetDashboardService implements GetDashboardUseCase {

    private final DashboardRepositoryPort dashboardRepositoryPort;

    public GetDashboardService(DashboardRepositoryPort dashboardRepositoryPort) {
        this.dashboardRepositoryPort = dashboardRepositoryPort;
    }

    @Override
    public DashboardSummary getDashboard(DashboardQuery query) {
        validateQuery(query);
        return dashboardRepositoryPort.findDashboard(query.year(), query.month());
    }

    private void validateQuery(DashboardQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("Dashboard query is required");
        }
        if (query.year() != null && query.year() < 1) {
            throw new IllegalArgumentException("Year must be greater than zero");
        }
        if (query.month() != null && (query.month() < 1 || query.month() > 12)) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
    }
}