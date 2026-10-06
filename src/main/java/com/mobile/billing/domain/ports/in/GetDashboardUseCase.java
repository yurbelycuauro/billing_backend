package com.mobile.billing.domain.ports.in;

import com.mobile.billing.domain.model.DashboardSummary;

public interface GetDashboardUseCase {

    DashboardSummary getDashboard(DashboardQuery query);

    record DashboardQuery(Integer year, Integer month) { }
}