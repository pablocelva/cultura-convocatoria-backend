package cl.tucultura.convocatorias_api.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import cl.tucultura.convocatorias_api.domain.model.Convocatoria;

public record ConvocatoriaRequestDTO(
    String titulo, String descripcion, String tipo, String categoria,
    BigDecimal monto, String moneda, LocalDateTime fechaApertura,
    LocalDateTime fechaCierre, String urlOficial,
    List<String> requisitos, List<String> documentacion, UUID fuenteId
) {
   public Convocatoria toDomain() {
    return new Convocatoria(
        null, this.titulo, this.descripcion,
            Convocatoria.TipoConvocatoria.valueOf(this.tipo.toUpperCase()),
            this.categoria, this.monto, this.moneda != null ? this.moneda : "CLP",
            this.fechaApertura, this.fechaCierre, this.urlOficial,
            null, 
            this.requisitos != null ? this.requisitos : List.of(),
            this.documentacion != null ? this.documentacion : List.of(),
            this.fuenteId
    );
   } 
}
