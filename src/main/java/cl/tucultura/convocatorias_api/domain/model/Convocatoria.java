package cl.tucultura.convocatorias_api.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import cl.tucultura.convocatorias_api.domain.valueobject.*;

public record Convocatoria(
    UUID id,
    Titulo titulo,
    Descripcion descripcion,
    TipoConvocatoria tipo,
    Categoria categoria,
    Monto monto,
    LocalDateTime fechaApertura,
    LocalDateTime fechaCierre,
    UrlOficial urlOficial,
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
