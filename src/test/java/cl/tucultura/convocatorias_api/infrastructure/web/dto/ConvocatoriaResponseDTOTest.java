package cl.tucultura.convocatorias_api.infrastructure.web.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cl.tucultura.convocatorias_api.domain.model.Convocatoria;

class ConvocatoriaResponseDTOTest {

    @Test
    void fromDomain_convierteTodosLosCampos() {
        UUID id = UUID.randomUUID();
        UUID fuenteId = UUID.randomUUID();
        LocalDateTime apertura = LocalDateTime.of(2026, 8, 1, 0, 0);
        LocalDateTime cierre = LocalDateTime.of(2026, 12, 31, 23, 59);

        Convocatoria dominio = new Convocatoria(
            id, "Fondo Artes", "Descripción del fondo",
            Convocatoria.TipoConvocatoria.FONDO, "Artes Visuales",
            BigDecimal.valueOf(3000000), "USD",
            apertura, cierre, "https://fondo.org",
            Convocatoria.EstadoConvocatoria.ABIERTA,
            List.of("Requisito 1", "Requisito 2"),
            List.of("Doc 1", "Doc 2", "Doc 3"),
            fuenteId
        );

        ConvocatoriaResponseDTO dto = ConvocatoriaResponseDTO.fromDomain(dominio);

        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals("Fondo Artes", dto.titulo());
        assertEquals("Descripción del fondo", dto.descripcion());
        assertEquals("FONDO", dto.tipo());
        assertEquals("Artes Visuales", dto.categoria());
        assertEquals(BigDecimal.valueOf(3000000), dto.monto());
        assertEquals("USD", dto.moneda());
        assertEquals(apertura, dto.fechaApertura());
        assertEquals(cierre, dto.fechaCierre());
        assertEquals("https://fondo.org", dto.urlOficial());
        assertEquals("ABIERTA", dto.estado());
        assertEquals(fuenteId, dto.fuenteId());
    }

    @Test
    void fromDomain_listasNoNulas_seMapean() {
        Convocatoria dominio = new Convocatoria(
            UUID.randomUUID(), "t", "d",
            Convocatoria.TipoConvocatoria.BECA, "cat",
            BigDecimal.ZERO, "CLP",
            LocalDateTime.now(), LocalDateTime.now(),
            "https://x.org", Convocatoria.EstadoConvocatoria.ABIERTA,
            List.of("a", "b"), List.of("x", "y", "z"), null
        );

        ConvocatoriaResponseDTO dto = ConvocatoriaResponseDTO.fromDomain(dominio);

        assertEquals(2, dto.requisitos().size());
        assertEquals(3, dto.documentacion().size());
        assertEquals("a", dto.requisitos().get(0));
        assertEquals("x", dto.documentacion().get(0));
    }
}
