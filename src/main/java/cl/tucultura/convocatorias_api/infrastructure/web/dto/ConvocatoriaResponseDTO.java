package cl.tucultura.convocatorias_api.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import cl.tucultura.convocatorias_api.domain.model.Convocatoria;

public record ConvocatoriaResponseDTO(
    UUID id, String titulo, String descripcion, String tipo, String categoria,
    BigDecimal monto, String moneda, LocalDateTime fechaApertura,
    LocalDateTime fechaCierre, String urlOficial, String estado,
    List<String> requisitos, List<String> documentacion, UUID fuenteId
) {
    public static ConvocatoriaResponseDTO fromDomain(Convocatoria c) {
        return new ConvocatoriaResponseDTO(
            c.id(), c.titulo(), c.descripcion(), c.tipo().name(), c.categoria(),
            c.monto(), c.moneda(), c.fechaApertura(), c.fechaCierre(),
            c.urlOficial(), c.estado().name(), c.requisitos(), c.documentacion(), c.fuenteId()
        );
    }
}    
