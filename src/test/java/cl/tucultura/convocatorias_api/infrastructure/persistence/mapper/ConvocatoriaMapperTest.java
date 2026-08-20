package cl.tucultura.convocatorias_api.infrastructure.persistence.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cl.tucultura.convocatorias_api.domain.model.Convocatoria;
import cl.tucultura.convocatorias_api.infrastructure.persistence.entity.ConvocatoriaEntity;

class ConvocatoriaMapperTest {

    private final ConvocatoriaMapper mapper = new ConvocatoriaMapper();

    private ConvocatoriaEntity crearEntity() {
        ConvocatoriaEntity e = new ConvocatoriaEntity();
        e.setId(UUID.randomUUID());
        e.setTitulo("Beca Música");
        e.setDescripcion("Beca para músicos");
        e.setTipo("BECA");
        e.setCategoria("Música");
        e.setMonto(BigDecimal.valueOf(5000000));
        e.setMoneda("CLP");
        e.setFechaApertura(LocalDateTime.of(2026, 8, 1, 0, 0));
        e.setFechaCierre(LocalDateTime.of(2026, 12, 31, 23, 59));
        e.setUrlOficial("https://ejemplo.org");
        e.setEstado("ABIERTA");
        e.setRequisitosJson("[\"Ser mayor de 18\",\"Residir en Chile\"]");
        e.setDocumentacionJson("[\"CV\",\"Portafolio\"]");
        e.setFuenteId(UUID.randomUUID());
        return e;
    }

    private Convocatoria crearDominio() {
        return new Convocatoria(
            UUID.randomUUID(), "Fondo Artes", "Fondo de financiamiento",
            Convocatoria.TipoConvocatoria.FONDO, "Artes Visuales",
            BigDecimal.valueOf(3000000), "CLP",
            LocalDateTime.of(2026, 9, 1, 0, 0),
            LocalDateTime.of(2026, 11, 30, 23, 59),
            "https://fondo.org", Convocatoria.EstadoConvocatoria.PROXIMAMENTE,
            List.of("Ser artista"), List.of("Propuesta"), UUID.randomUUID()
        );
    }

    @Test
    void toDomain_convierteEntityADominioCorrectamente() {
        ConvocatoriaEntity e = crearEntity();
        Convocatoria d = mapper.toDomain(e);

        assertEquals(e.getId(), d.id());
        assertEquals("Beca Música", d.titulo());
        assertEquals("Beca para músicos", d.descripcion());
        assertEquals(Convocatoria.TipoConvocatoria.BECA, d.tipo());
        assertEquals("Música", d.categoria());
        assertEquals(BigDecimal.valueOf(5000000), d.monto());
        assertEquals("CLP", d.moneda());
        assertEquals("https://ejemplo.org", d.urlOficial());
        assertEquals(Convocatoria.EstadoConvocatoria.ABIERTA, d.estado());
        assertEquals(2, d.requisitos().size());
        assertEquals(2, d.documentacion().size());
        assertEquals(e.getFuenteId(), d.fuenteId());
    }

    @Test
    void toDomain_conEntityNull_retornaNull() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    void toDomain_requisitosJson_parseaJsonCorrectamente() {
        ConvocatoriaEntity e = crearEntity();
        e.setRequisitosJson("[\"a\",\"b\",\"c\"]");
        Convocatoria d = mapper.toDomain(e);
        assertEquals(List.of("a", "b", "c"), d.requisitos());
    }

    @Test
    void toDomain_requisitosJsonNull_retornaListaVacia() {
        ConvocatoriaEntity e = crearEntity();
        e.setRequisitosJson(null);
        Convocatoria d = mapper.toDomain(e);
        assertTrue(d.requisitos().isEmpty());
    }

    @Test
    void toDomain_requisitosJsonInvalido_retornaListaVacia() {
        ConvocatoriaEntity e = crearEntity();
        e.setRequisitosJson("no-es-json");
        Convocatoria d = mapper.toDomain(e);
        assertTrue(d.requisitos().isEmpty());
    }

    @Test
    void toEntity_convierteDominioAEntityCorrectamente() {
        Convocatoria d = crearDominio();
        ConvocatoriaEntity e = mapper.toEntity(d);

        assertNotNull(e.getId());
        assertEquals("Fondo Artes", e.getTitulo());
        assertEquals("Fondo de financiamiento", e.getDescripcion());
        assertEquals("FONDO", e.getTipo());
        assertEquals("Artes Visuales", e.getCategoria());
        assertEquals(BigDecimal.valueOf(3000000), e.getMonto());
        assertEquals("CLP", e.getMoneda());
        assertEquals("https://fondo.org", e.getUrlOficial());
        assertEquals("PROXIMAMENTE", e.getEstado());
        assertEquals(d.fuenteId(), e.getFuenteId());
    }

    @Test
    void toEntity_conDomainNull_retornaNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toEntity_idNull_generaUuidAutomatico() {
        Convocatoria d = crearDominio();
        ConvocatoriaEntity e = mapper.toEntity(new Convocatoria(
            null, d.titulo(), d.descripcion(), d.tipo(), d.categoria(),
            d.monto(), d.moneda(), d.fechaApertura(), d.fechaCierre(),
            d.urlOficial(), d.estado(), d.requisitos(), d.documentacion(), d.fuenteId()
        ));
        assertNotNull(e.getId());
    }

    @Test
    void toEntity_requisitosNull_serializeaComoArrayVacio() {
        Convocatoria d = new Convocatoria(
            UUID.randomUUID(), "t", "d",
            Convocatoria.TipoConvocatoria.BECA, "cat",
            BigDecimal.ZERO, "CLP",
            LocalDateTime.now(), LocalDateTime.now(),
            "https://x.org", Convocatoria.EstadoConvocatoria.ABIERTA,
            null, null, null
        );
        ConvocatoriaEntity e = mapper.toEntity(d);
        assertEquals("[]", e.getRequisitosJson());
        assertEquals("[]", e.getDocumentacionJson());
    }
}
