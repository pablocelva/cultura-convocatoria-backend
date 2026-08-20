package cl.tucultura.convocatorias_api.infrastructure.web.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.tucultura.convocatorias_api.domain.model.Convocatoria;
import cl.tucultura.convocatorias_api.domain.valueobject.*;

@DisplayName("Convocatoria Response DTO")
class ConvocatoriaResponseDTOTest {

    @Test
    @DisplayName("Should convert all domain fields to response")
    void fromDomain_convierteTodosLosCampos() {
        UUID id = UUID.randomUUID();
        UUID fuenteId = UUID.randomUUID();
        LocalDateTime apertura = LocalDateTime.of(2026, 8, 1, 0, 0);
        LocalDateTime cierre = LocalDateTime.of(2026, 12, 31, 23, 59);

        Convocatoria dominio = new Convocatoria(
            id, new Titulo("Fondo Artes"), new Descripcion("Descripción del fondo"),
            Convocatoria.TipoConvocatoria.FONDO, new Categoria("Artes Visuales"),
            new Monto(BigDecimal.valueOf(3000000), "USD"),
            apertura, cierre, new UrlOficial("https://fondo.org"),
            Convocatoria.EstadoConvocatoria.ABIERTA,
            List.of("Requisito 1", "Requisito 2"),
            List.of("Doc 1", "Doc 2", "Doc 3"),
            fuenteId
        );

        ConvocatoriaResponseDTO dto = ConvocatoriaResponseDTO.fromDomain(dominio);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.titulo()).isEqualTo("Fondo Artes");
        assertThat(dto.descripcion()).isEqualTo("Descripción del fondo");
        assertThat(dto.tipo()).isEqualTo("FONDO");
        assertThat(dto.categoria()).isEqualTo("Artes Visuales");
        assertThat(dto.monto()).isEqualByComparingTo(BigDecimal.valueOf(3000000));
        assertThat(dto.moneda()).isEqualTo("USD");
        assertThat(dto.fechaApertura()).isEqualTo(apertura);
        assertThat(dto.fechaCierre()).isEqualTo(cierre);
        assertThat(dto.urlOficial()).isEqualTo("https://fondo.org");
        assertThat(dto.estado()).isEqualTo("ABIERTA");
        assertThat(dto.fuenteId()).isEqualTo(fuenteId);
    }

    @Test
    @DisplayName("Should map non-null lists correctly")
    void fromDomain_listasNoNulas_seMapean() {
        Convocatoria dominio = new Convocatoria(
            UUID.randomUUID(), new Titulo("t"), new Descripcion("d"),
            Convocatoria.TipoConvocatoria.BECA, new Categoria("cat"),
            new Monto(BigDecimal.ZERO, "CLP"),
            LocalDateTime.now(), LocalDateTime.now(),
            new UrlOficial("https://x.org"), Convocatoria.EstadoConvocatoria.ABIERTA,
            List.of("a", "b"), List.of("x", "y", "z"), null
        );

        ConvocatoriaResponseDTO dto = ConvocatoriaResponseDTO.fromDomain(dominio);

        assertThat(dto.requisitos()).hasSize(2);
        assertThat(dto.documentacion()).hasSize(3);
        assertThat(dto.requisitos().get(0)).isEqualTo("a");
        assertThat(dto.documentacion().get(0)).isEqualTo("x");
    }

    @Test
    @DisplayName("Should handle null monto correctly")
    void fromDomain_montoNull_montoYMonedaNull() {
        Convocatoria dominio = new Convocatoria(
            UUID.randomUUID(), new Titulo("t"), new Descripcion("d"),
            Convocatoria.TipoConvocatoria.BECA, new Categoria("cat"),
            null,
            LocalDateTime.now(), LocalDateTime.now(),
            new UrlOficial("https://x.org"), Convocatoria.EstadoConvocatoria.ABIERTA,
            List.of(), List.of(), null
        );

        ConvocatoriaResponseDTO dto = ConvocatoriaResponseDTO.fromDomain(dominio);

        assertThat(dto.monto()).isNull();
        assertThat(dto.moneda()).isNull();
    }
}