package com.mobile.billing.infrastructure.adapter.out.client;

import com.mobile.billing.domain.ports.out.ExternalExchangeRatePort;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.JacksonException;

@Component
public class BancoCentralClientAdapter implements ExternalExchangeRatePort {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String token;
    private final String seriesId;
    private final String seriesFromCurrency;
    private final String seriesToCurrency;

    public BancoCentralClientAdapter(
            ObjectMapper objectMapper,
            @Value("${app.exchange-rate.banco-central.base-url}") String baseUrl,
            @Value("${app.exchange-rate.banco-central.token:}") String token,
            @Value("${app.exchange-rate.banco-central.series-id}") String seriesId,
            @Value("${app.exchange-rate.banco-central.from-currency}") String seriesFromCurrency,
            @Value("${app.exchange-rate.banco-central.to-currency}") String seriesToCurrency) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .build();
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
        this.token = token;
        this.seriesId = seriesId;
        this.seriesFromCurrency = seriesFromCurrency.toUpperCase(Locale.ROOT);
        this.seriesToCurrency = seriesToCurrency.toUpperCase(Locale.ROOT);
    }

    @Override
    public Optional<BigDecimal> fetchRate(String fromCurrency, String toCurrency, LocalDate date) {
        String from = fromCurrency.toUpperCase(Locale.ROOT);
        String to = toCurrency.toUpperCase(Locale.ROOT);
        boolean direct = from.equals(seriesFromCurrency) && to.equals(seriesToCurrency);
        boolean inverse = from.equals(seriesToCurrency) && to.equals(seriesFromCurrency);
        // if (!direct && !inverse) {
        // return Optional.empty();
        // }
        if (token.isBlank()) {
            throw new IllegalStateException("Banco Central API token is not configured.");
        }

        HttpRequest request = HttpRequest.newBuilder(buildUri(date))
                .header("Accept", "application/json")
                .timeout(REQUEST_TIMEOUT)
                .GET()
                .build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Banco Central exchange-rate request was interrupted.");
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to communicate with Banco Central exchange-rate service.");
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException(
                    "Banco Central exchange-rate service returned HTTP " + response.statusCode() + ".");
        }

        Optional<BigDecimal> rate = readRate(response.body());
        if (inverse && rate.isPresent()) {
            return Optional.of(BigDecimal.ONE.divide(rate.get(), 6, RoundingMode.HALF_UP));
        }
        return rate;
    }

    private URI buildUri(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Exchange-rate date is required.");
        }
        String query = "token=" + encodeQueryValue(token)
                + "&function=GetSeries"
                + "&timeseries=" + encodeQueryValue(seriesId)
                + "&firstdate=" + date
                + "&lastdate=" + date;
        return URI.create(baseUrl + "?" + query);
    }

    private String encodeQueryValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private Optional<BigDecimal> readRate(String responseBody) {
        try {
            JsonNode observations = objectMapper.readTree(responseBody)
                    .path("Series")
                    .path("Obs");
            if (!observations.isArray()) {
                throw new IllegalStateException("Banco Central response does not contain a series observation list.");
            }
            if (observations.isEmpty()) {
                return Optional.empty();
            }

            String value = observations.get(0).path("value").asText();
            if (value.isBlank()) {
                throw new IllegalStateException("Banco Central response contains an observation without a value.");
            }
            BigDecimal rate = new BigDecimal(value);
            if (rate.signum() <= 0) {
                throw new IllegalStateException("Banco Central response contains a non-positive exchange rate.");
            }
            return Optional.of(rate);
        } catch (JacksonException | NumberFormatException exception) {
            throw new IllegalStateException("Unable to parse Banco Central exchange-rate response.");
        }
    }
}
