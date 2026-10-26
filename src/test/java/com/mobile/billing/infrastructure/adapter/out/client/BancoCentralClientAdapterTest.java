package com.mobile.billing.infrastructure.adapter.out.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class BancoCentralClientAdapterTest {

    private HttpServer server;
    private AtomicReference<String> requestQuery;
    private BancoCentralClientAdapter adapter;

    @BeforeEach
    void setUp() throws Exception {
        requestQuery = new AtomicReference<>();
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/SieteRestWS.ashx", exchange -> {
            requestQuery.set(exchange.getRequestURI().getRawQuery());
            byte[] body = """
                    {"Series":{"Obs":[{"indexDateString":"09-10-2026","value":"934.14"}]}}
                    """.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            try (var responseBody = exchange.getResponseBody()) {
                responseBody.write(body);
            }
        });
        server.start();
        adapter = new BancoCentralClientAdapter(
                JsonMapper.builder().build(),
                "http://localhost:" + server.getAddress().getPort() + "/SieteRestWS.ashx",
                "test-token",
                "F073.TCO.PRE.Z.D",
                "USD",
                "CLP");
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void fetchesTheRequestedSeriesAndDateFromBancoCentral() {
        var rate = adapter.fetchRate("USD", "CLP", LocalDate.of(2026, 10, 9));

        assertEquals(new BigDecimal("934.14"), rate.orElseThrow());
        assertTrue(requestQuery.get().contains("token=test-token"));
        assertTrue(requestQuery.get().contains("function=GetSeries"));
        assertTrue(requestQuery.get().contains("timeseries=F073.TCO.PRE.Z.D"));
        assertTrue(requestQuery.get().contains("firstdate=2026-10-09"));
        assertTrue(requestQuery.get().contains("lastdate=2026-10-09"));
    }

    @Test
    void returnsTheInverseWhenTheRequestedCurrencyPairIsReversed() {
        var rate = adapter.fetchRate("CLP", "USD", LocalDate.of(2026, 10, 9));

        assertEquals(new BigDecimal("0.001071"), rate.orElseThrow());
    }

    @Test
    void returnsEmptyForUnsupportedCurrencyPairs() {
        var rate = adapter.fetchRate("EUR", "CLP", LocalDate.of(2026, 10, 9));

        assertFalse(rate.isPresent());
        assertNull(requestQuery.get());
    }
}
