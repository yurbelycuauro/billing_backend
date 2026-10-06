package com.mobile.billing.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.mobile.billing.domain.model.DashboardSummary;
import com.mobile.billing.domain.model.InvoiceStatus;
import com.mobile.billing.domain.model.InvoiceStatusSummary;
import com.mobile.billing.domain.ports.in.GetDashboardUseCase.DashboardQuery;
import com.mobile.billing.domain.ports.out.DashboardRepositoryPort;

class GetDashboardServiceTest {

    @Test
    void shouldReturnDashboardForTheRequestedPeriod() {
        DashboardRepositoryPort repository = mock(DashboardRepositoryPort.class);
        DashboardSummary expected = new DashboardSummary(List.of(), List.of(), List.of());
        when(repository.findDashboard(2026, 9)).thenReturn(expected);

        DashboardSummary result = new GetDashboardService(repository)
                .getDashboard(new DashboardQuery(2026, 9));

        assertEquals(expected, result);
        verify(repository).findDashboard(2026, 9);
    }

    @Test
    void shouldRejectInvalidMonth() {
        DashboardRepositoryPort repository = mock(DashboardRepositoryPort.class);

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new GetDashboardService(repository)
                        .getDashboard(new DashboardQuery(2026, 13))
        );
    }

    @Test
    void shouldBuildInvoiceStatusSummaryContract() {
        InvoiceStatusSummary summary = InvoiceStatusSummary.from(InvoiceStatus.APPROVED, 928, 1254);

        assertEquals("APROVED", summary.status());
        assertEquals("74%", summary.percentage());
        assertEquals(928, summary.count());
        assertEquals(74, summary.value());
        assertEquals("#10B981", summary.color());
        assertEquals("aproved", summary.type());
    }

    @Test
    void shouldReturnZeroPercentageWhenThereAreNoInvoices() {
        InvoiceStatusSummary summary = InvoiceStatusSummary.from(InvoiceStatus.APPROVED, 0, 0);

        assertEquals("0%", summary.percentage());
        assertEquals(0, summary.value());
    }
}