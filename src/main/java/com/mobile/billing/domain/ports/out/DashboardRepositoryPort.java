package com.mobile.billing.domain.ports.out;

import com.mobile.billing.domain.model.DashboardSummary;

public interface DashboardRepositoryPort {

    DashboardSummary findDashboard(Integer year, Integer month);
}