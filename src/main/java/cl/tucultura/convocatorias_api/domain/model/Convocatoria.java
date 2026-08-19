package cl.tucultura.convocatorias_api.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Convocatoria(
    UUID id,
    String titulo,
    String descripcion,
    TipoConvocatoria tipo,
    String categoria,
    BigDecimal monto,
    String moneda,
    LocalDateTime fechaApertura,
    LocalDateTime fechaCierre,
    String urlOficial,
    EstadoConvocatoria estado,
    List<String> requisitos,
    List<String> documentacion,
    UUID fuenteId
) {
    public enum TipoConvocatoria {
        BECA, FONDO, RESIDENCIA, PREMIO, CONVOCATORIA
    }

    public enum EstadoConvocatoria {
        ABIERTA, PROXIMAMENTE, CERRADA, CANCELADA
    }

    public boolean estavigente() {
        LocalDateTime ahora = LocalDateTime.now();
        return ahora.isAfter(fechaApertura) && ahora.isBefore(fechaCierre);
    }
}
