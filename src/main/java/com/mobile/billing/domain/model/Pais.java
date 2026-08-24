package com.mobile.billing.domain.model;

import java.math.BigDecimal;

public enum Pais {

    PE("Perú", "PEN", new BigDecimal("0.18")),       // IGV 18%
    CL("Chile", "CLP", new BigDecimal("0.19")),      // IVA 19%
    MX("México", "MXN", new BigDecimal("0.16")),     // IVA 16%
    US("Estados Unidos", "USD", BigDecimal.ZERO);    // Sin IVA nacional

    private final String nombre;
    private final String codigoMoneda;
    private final BigDecimal tasaImpuesto;

    Pais(String nombre, String codigoMoneda, BigDecimal tasaImpuesto) {
        this.nombre = nombre;
        this.codigoMoneda = codigoMoneda;
        this.tasaImpuesto = tasaImpuesto;
    }

    public String getNombre() { return nombre; }
    public String getCodigoMoneda() { return codigoMoneda; }
    public BigDecimal getTasaImpuesto() { return tasaImpuesto; }

    // Método para buscar un país por su código (ej: "PE" o "pe")
    public static Pais obtenerPorCodigo(String codigo) {
        for (Pais p : values()) {
            if (p.name().equalsIgnoreCase(codigo)) {
                return p;
            }
        }
        throw new IllegalArgumentException("País no soportado: " + codigo);
    }
}
