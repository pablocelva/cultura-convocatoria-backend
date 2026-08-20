package cl.tucultura.convocatorias_api.infrastructure.web.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cl.tucultura.convocatorias_api.domain.model.Convocatoria;

class ConvocatoriaRequestDTOTest {

    @Test
    void toDomain_camposCompletos_convierteCorrectamente() {
        UUID fuenteId = UUID.randomUUID();
        ConvocatoriaRequestDTO dto = new ConvocatoriaRequestDTO(
            "Beca Música", "Descripción", "BECA", "Música",
            BigDecimal.valueOf(5000000), "CLP",
            LocalDateTime.of(2026, 8, 1, 0, 0),
            LocalDateTime.of(2026, 12, 31, 23, 59),
            "https://ejemplo.org",
            List.of("Requisito 1"), List.of("Doc 1"), fuenteId
        );

        Convocatoria d = dto.toDomain();

        assertNotNull(d);
        assertEquals("Beca Música", d.titulo());
        assertEquals("Descripción", d.descripcion());
        assertEquals(Convocatoria.TipoConvocatoria.BECA, d.tipo());
        assertEquals("Música", d.categoria());
        assertEquals(BigDecimal.valueOf(5000000), d.monto());
        assertEquals("CLP", d.moneda());
        assertEquals("https://ejemplo.org", d.urlOficial());
        assertEquals(List.of("Requisito 1"), d.requisitos());
        assertEquals(List.of("Doc 1"), d.documentacion());
        assertEquals(fuenteId, d.fuenteId());
        assertEquals(null, d.id());
        assertEquals(null, d.estado());
    }

    @Test
    void toDomain_monedaNull_defaultClp() {
        ConvocatoriaRequestDTO dto = new ConvocatoriaRequestDTO(
            "t", "d", "BECA", "cat",
            BigDecimal.ZERO, null,
            LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            "https://x.org", null, null, null
        );
        assertEquals("CLP", dto.toDomain().moneda());
    }

    @Test
    void toDomain_requisitosNull_defaultListaVacia() {
        ConvocatoriaRequestDTO dto = new ConvocatoriaRequestDTO(
            "t", "d", "BECA", "cat",
            BigDecimal.ZERO, "CLP",
            LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            "https://x.org", null, null, null
        );
        assertTrue(dto.toDomain().requisitos().isEmpty());
    }

    @Test
    void toDomain_documentacionNull_defaultListaVacia() {
        ConvocatoriaRequestDTO dto = new ConvocatoriaRequestDTO(
            "t", "d", "BECA", "cat",
            BigDecimal.ZERO, "CLP",
            LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            "https://x.org", null, null, null
        );
        assertTrue(dto.toDomain().documentacion().isEmpty());
    }

    @Test
    void toDomain_tipoMinusculas_seConvierteAMayusculas() {
        ConvocatoriaRequestDTO dto = new ConvocatoriaRequestDTO(
            "t", "d", "beca", "cat",
            BigDecimal.ZERO, "CLP",
            LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            "https://x.org", null, null, null
        );
        assertEquals(Convocatoria.TipoConvocatoria.BECA, dto.toDomain().tipo());
    }

    @Test
    void toDomain_fechaCierreIgualAApertura_lanzaExcepcion() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(5);
        ConvocatoriaRequestDTO dto = new ConvocatoriaRequestDTO(
            "t", "d", "BECA", "cat",
            BigDecimal.ZERO, "CLP",
            fecha, fecha,
            "https://x.org", null, null, null
        );
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, dto::toDomain);
        assertEquals("La fecha de cierre debe ser posterior a la fecha de apertura.", ex.getMessage());
    }

    @Test
    void toDomain_fechaCierreAntesDeApertura_lanzaExcepcion() {
        LocalDateTime apertura = LocalDateTime.now().plusDays(10);
        LocalDateTime cierre = LocalDateTime.now().plusDays(5);
        ConvocatoriaRequestDTO dto = new ConvocatoriaRequestDTO(
            "t", "d", "BECA", "cat",
            BigDecimal.ZERO, "CLP",
            apertura, cierre,
            "https://x.org", null, null, null
        );
        assertThrows(IllegalArgumentException.class, dto::toDomain);
    }
}
