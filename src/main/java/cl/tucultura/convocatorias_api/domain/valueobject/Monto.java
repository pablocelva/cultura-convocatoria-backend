package cl.tucultura.convocatorias_api.domain.valueobject;

import java.math.BigDecimal;

public record Monto(BigDecimal value, String moneda) {
    public Monto {
        if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo");
        }
        moneda = (moneda != null && !moneda.isBlank()) ? moneda.toUpperCase() : "CLP";
    }
    
    public static Monto crear(BigDecimal value, String moneda) {
        return new Monto(value, moneda);
    }
}
