package com.mobile.billing.domain.model;

public enum Moneda {

    PEN("Soles", "S/", 2),
    CLP("Pesos", "$", 0),
    USD("Dólar Estadounidense", "US$", 2),
    EUR("Euro", "€", 2);

    private final String nombre;
    private final String simbolo;
    private final int decimales;

    Moneda(String nombre, String simbolo, int decimales) {
        this.nombre = nombre;
        this.simbolo = simbolo;
        this.decimales = decimales;
    }

    // Getters para acceder a las propiedades
    public String getNombre() { return nombre; }
    public String getSimbolo() { return simbolo; }
    public int getDecimales() { return decimales; }

    // Método de negocio para buscar por texto (ej: "USD" o "Dólar")
    public static Moneda obtenerPorCodigo(String codigo) {
        for (Moneda m : values()) {
            if (m.name().equalsIgnoreCase(codigo)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Moneda no soportada: " + codigo);
    }
}
