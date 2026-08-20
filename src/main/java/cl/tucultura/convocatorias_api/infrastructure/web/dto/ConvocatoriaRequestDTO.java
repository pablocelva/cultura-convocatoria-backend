package cl.tucultura.convocatorias_api.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.validator.constraints.URL;

import cl.tucultura.convocatorias_api.domain.model.Convocatoria;
import cl.tucultura.convocatorias_api.domain.valueobject.*;
import jakarta.validation.constraints.*;

public record ConvocatoriaRequestDTO(
    
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255, message = "El título no puede superar los 255 caracteres")
    String titulo,

    @NotBlank(message = "La descripción es obligatoria")
    String descripcion,

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(regexp = "BECA|FONDO|RESIDENCIA|PREMIO|CONVOCATORIA", message = "Tipo inválido")
    String tipo,

    @NotBlank(message = "La categoría es obligatoria")
    String categoria,

    @DecimalMin(value = "0.0", inclusive = true, message = "El monto no puede ser negativo")
    BigDecimal monto,

    String moneda,

    @NotNull(message = "La fecha de apertura es obligatoria")
    @FutureOrPresent(message = "La fecha de apertura no puede ser en el pasado")
    LocalDateTime fechaApertura,

    @NotNull(message = "La fecha de cierre es obligatoria")
    @Future(message = "La fecha de cierre debe ser en el futuro")
    LocalDateTime fechaCierre,

    @NotBlank(message = "La URL oficial es obligatoria")
    @URL(message = "La URL debe tener un formato válido")
    String urlOficial,

    List<String> requisitos,
    List<String> documentacion,
    UUID fuenteId
) {
    public Convocatoria toDomain() {
        if (fechaCierre.isBefore(fechaApertura) || fechaCierre.isEqual(fechaApertura)) {
            throw new IllegalArgumentException("La fecha de cierre debe ser posterior a la fecha de apertura.");
        }

        return new Convocatoria(
            null, 
            new Titulo(this.titulo), 
            new Descripcion(this.descripcion),
            Convocatoria.TipoConvocatoria.valueOf(this.tipo.toUpperCase()),
            new Categoria(this.categoria), 
            this.monto != null ? new Monto(this.monto, this.moneda) : null,
            this.fechaApertura,
            this.fechaCierre,
            new UrlOficial(this.urlOficial),
            null, 
            this.requisitos != null ? this.requisitos : List.of(),
            this.documentacion != null ? this.documentacion : List.of(),
            this.fuenteId
        );
    }
}