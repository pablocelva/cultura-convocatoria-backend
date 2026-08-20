package cl.tucultura.convocatorias_api.infrastructure.web.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.tucultura.convocatorias_api.domain.exception.FechaCierreInvalidaException;
import cl.tucultura.convocatorias_api.domain.model.Convocatoria;

@DisplayName("Convocatoria Request DTO")
class ConvocatoriaRequestDTOTest {

    @Test
    @DisplayName("Should convert all fields to domain correctly")
    void toDomain_camposCompletos_convierteCorrectamente() {
        UUID fuenteId = UUID.randomUUID();
        ConvocatoriaRequestDTO dto = new ConvocatoriaRequestDTO(
            "Beca Música", "Descripción", "BECA", "Música",
            BigDecimal.valueOf(5000000), "CLP",
            LocalDateTime.of(2026, 9, 1, 0, 0),
            LocalDateTime.of(2026, 12, 31, 23, 59),
            "https://ejemplo.org",
            List.of("Requisito 1"), List.of("Doc 1"), fuenteId
        );

        Convocatoria d = dto.toDomain();

        assertThat(d).isNotNull();
        assertThat(d.titulo().value()).isEqualTo("Beca Música");
        assertThat(d.descripcion().value()).isEqualTo("Descripción");
        assertThat(d.tipo()).isEqualTo(Convocatoria.TipoConvocatoria.BECA);
        assertThat(d.categoria().value()).isEqualTo("Música");
        assertThat(d.monto().value()).isEqualByComparingTo(BigDecimal.valueOf(5000000));
        assertThat(d.monto().moneda()).isEqualTo("CLP");
        assertThat(d.urlOficial().value()).isEqualTo("https://ejemplo.org");
        assertThat(d.requisitos()).containsExactly("Requisito 1");
        assertThat(d.documentacion()).containsExactly("Doc 1");
        assertThat(d.fuenteId()).isEqualTo(fuenteId);
        assertThat(d.id()).isNull();
        assertThat(d.estado()).isNull();
    }

    @Test
    @DisplayName("Should default moneda to CLP when null")
    void toDomain_monedaNull_defaultClp() {
        ConvocatoriaRequestDTO dto = new ConvocatoriaRequestDTO(
            "t", "d", "BECA", "cat",
            BigDecimal.ZERO, null,
            LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            "https://x.org", null, null, null
        );
        assertThat(dto.toDomain().monto().moneda()).isEqualTo("CLP");
    }

    @Test
    @DisplayName("Should default requisitos to empty list when null")
    void toDomain_requisitosNull_defaultListaVacia() {
        ConvocatoriaRequestDTO dto = new ConvocatoriaRequestDTO(
            "t", "d", "BECA", "cat",
            BigDecimal.ZERO, "CLP",
            LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            "https://x.org", null, null, null
        );
        assertThat(dto.toDomain().requisitos()).isEmpty();
    }

    @Test
    @DisplayName("Should default documentacion to empty list when null")
    void toDomain_documentacionNull_defaultListaVacia() {
        ConvocatoriaRequestDTO dto = new ConvocatoriaRequestDTO(
            "t", "d", "BECA", "cat",
            BigDecimal.ZERO, "CLP",
            LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            "https://x.org", null, null, null
        );
        assertThat(dto.toDomain().documentacion()).isEmpty();
    }

    @Test
    @DisplayName("Should convert tipo to uppercase")
    void toDomain_tipoMinusculas_seConvierteAMayusculas() {
        ConvocatoriaRequestDTO dto = new ConvocatoriaRequestDTO(
            "t", "d", "beca", "cat",
            BigDecimal.ZERO, "CLP",
            LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            "https://x.org", null, null, null
        );
        assertThat(dto.toDomain().tipo()).isEqualTo(Convocatoria.TipoConvocatoria.BECA);
    }

    @Test
    @DisplayName("Should throw exception when fechaCierre equals fechaApertura")
    void toDomain_fechaCierreIgualAApertura_lanzaExcepcion() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(5);
        ConvocatoriaRequestDTO dto = new ConvocatoriaRequestDTO(
            "t", "d", "BECA", "cat",
            BigDecimal.ZERO, "CLP",
            fecha, fecha,
            "https://x.org", null, null, null
        );
        assertThatThrownBy(dto::toDomain)
            .isInstanceOf(FechaCierreInvalidaException.class)
            .hasMessage("La fecha de cierre debe ser posterior a la fecha de apertura.");
    }

    @Test
    @DisplayName("Should throw exception when fechaCierre is before fechaApertura")
    void toDomain_fechaCierreAntesDeApertura_lanzaExcepcion() {
        LocalDateTime apertura = LocalDateTime.now().plusDays(10);
        LocalDateTime cierre = LocalDateTime.now().plusDays(5);
        ConvocatoriaRequestDTO dto = new ConvocatoriaRequestDTO(
            "t", "d", "BECA", "cat",
            BigDecimal.ZERO, "CLP",
            apertura, cierre,
            "https://x.org", null, null, null
        );
        assertThatThrownBy(dto::toDomain)
            .isInstanceOf(FechaCierreInvalidaException.class);
    }

    @Test
    @DisplayName("Should create domain with null monto when monto is not provided")
    void toDomain_montoNull_domainMontoEsNull() {
        ConvocatoriaRequestDTO dto = new ConvocatoriaRequestDTO(
            "t", "d", "BECA", "cat",
            null, null,
            LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            "https://x.org", null, null, null
        );
        assertThat(dto.toDomain().monto()).isNull();
    }
}