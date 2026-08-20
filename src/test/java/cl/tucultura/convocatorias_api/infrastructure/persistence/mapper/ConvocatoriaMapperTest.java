package cl.tucultura.convocatorias_api.infrastructure.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.tucultura.convocatorias_api.domain.model.Convocatoria;
import cl.tucultura.convocatorias_api.domain.valueobject.*;
import cl.tucultura.convocatorias_api.infrastructure.persistence.entity.ConvocatoriaEntity;

@DisplayName("Convocatoria Mapper")
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
            UUID.randomUUID(), new Titulo("Fondo Artes"), new Descripcion("Fondo de financiamiento"),
            Convocatoria.TipoConvocatoria.FONDO, new Categoria("Artes Visuales"),
            new Monto(BigDecimal.valueOf(3000000), "CLP"),
            LocalDateTime.of(2026, 9, 1, 0, 0),
            LocalDateTime.of(2026, 11, 30, 23, 59),
            new UrlOficial("https://fondo.org"), Convocatoria.EstadoConvocatoria.PROXIMAMENTE,
            List.of("Ser artista"), List.of("Propuesta"), UUID.randomUUID()
        );
    }

    @Test
    @DisplayName("Should convert entity to domain correctly")
    void toDomain_convierteEntityADominioCorrectamente() {
        ConvocatoriaEntity e = crearEntity();
        Convocatoria d = mapper.toDomain(e);

        assertThat(d.id()).isEqualTo(e.getId());
        assertThat(d.titulo().value()).isEqualTo("Beca Música");
        assertThat(d.descripcion().value()).isEqualTo("Beca para músicos");
        assertThat(d.tipo()).isEqualTo(Convocatoria.TipoConvocatoria.BECA);
        assertThat(d.categoria().value()).isEqualTo("Música");
        assertThat(d.monto().value()).isEqualByComparingTo(BigDecimal.valueOf(5000000));
        assertThat(d.monto().moneda()).isEqualTo("CLP");
        assertThat(d.urlOficial().value()).isEqualTo("https://ejemplo.org");
        assertThat(d.estado()).isEqualTo(Convocatoria.EstadoConvocatoria.ABIERTA);
        assertThat(d.requisitos()).hasSize(2);
        assertThat(d.documentacion()).hasSize(2);
        assertThat(d.fuenteId()).isEqualTo(e.getFuenteId());
    }

    @Test
    @DisplayName("Should return null when entity is null")
    void toDomain_conEntityNull_retornaNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("Should parse requisitos JSON correctly")
    void toDomain_requisitosJson_parseaJsonCorrectamente() {
        ConvocatoriaEntity e = crearEntity();
        e.setRequisitosJson("[\"a\",\"b\",\"c\"]");
        Convocatoria d = mapper.toDomain(e);
        assertThat(d.requisitos()).containsExactly("a", "b", "c");
    }

    @Test
    @DisplayName("Should return empty list when requisitos JSON is null")
    void toDomain_requisitosJsonNull_retornaListaVacia() {
        ConvocatoriaEntity e = crearEntity();
        e.setRequisitosJson(null);
        Convocatoria d = mapper.toDomain(e);
        assertThat(d.requisitos()).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when requisitos JSON is invalid")
    void toDomain_requisitosJsonInvalido_retornaListaVacia() {
        ConvocatoriaEntity e = crearEntity();
        e.setRequisitosJson("no-es-json");
        Convocatoria d = mapper.toDomain(e);
        assertThat(d.requisitos()).isEmpty();
    }

    @Test
    @DisplayName("Should convert domain to entity correctly")
    void toEntity_convierteDominioAEntityCorrectamente() {
        Convocatoria d = crearDominio();
        ConvocatoriaEntity e = mapper.toEntity(d);

        assertThat(e.getId()).isNotNull();
        assertThat(e.getTitulo()).isEqualTo("Fondo Artes");
        assertThat(e.getDescripcion()).isEqualTo("Fondo de financiamiento");
        assertThat(e.getTipo()).isEqualTo("FONDO");
        assertThat(e.getCategoria()).isEqualTo("Artes Visuales");
        assertThat(e.getMonto()).isEqualByComparingTo(BigDecimal.valueOf(3000000));
        assertThat(e.getMoneda()).isEqualTo("CLP");
        assertThat(e.getUrlOficial()).isEqualTo("https://fondo.org");
        assertThat(e.getEstado()).isEqualTo("PROXIMAMENTE");
        assertThat(e.getFuenteId()).isEqualTo(d.fuenteId());
    }

    @Test
    @DisplayName("Should return null when domain is null")
    void toEntity_conDomainNull_retornaNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("Should generate UUID automatically when id is null")
    void toEntity_idNull_generaUuidAutomatico() {
        Convocatoria d = crearDominio();
        ConvocatoriaEntity e = mapper.toEntity(new Convocatoria(
            null, d.titulo(), d.descripcion(), d.tipo(), d.categoria(),
            d.monto(), d.fechaApertura(), d.fechaCierre(),
            d.urlOficial(), d.estado(), d.requisitos(), d.documentacion(), d.fuenteId()
        ));
        assertThat(e.getId()).isNotNull();
    }

    @Test
    @DisplayName("Should serialize null requisitos as empty array")
    void toEntity_requisitosNull_serializeaComoArrayVacio() {
        Convocatoria d = new Convocatoria(
            UUID.randomUUID(), new Titulo("t"), new Descripcion("d"),
            Convocatoria.TipoConvocatoria.BECA, new Categoria("cat"),
            null,
            LocalDateTime.now(), LocalDateTime.now(),
            new UrlOficial("https://x.org"), Convocatoria.EstadoConvocatoria.ABIERTA,
            null, null, null
        );
        ConvocatoriaEntity e = mapper.toEntity(d);
        assertThat(e.getRequisitosJson()).isEqualTo("[]");
        assertThat(e.getDocumentacionJson()).isEqualTo("[]");
    }

    @Test
    @DisplayName("Should handle null monto correctly")
    void toEntity_montoNull_guardaNullEnEntity() {
        Convocatoria d = new Convocatoria(
            UUID.randomUUID(), new Titulo("t"), new Descripcion("d"),
            Convocatoria.TipoConvocatoria.BECA, new Categoria("cat"),
            null,
            LocalDateTime.now(), LocalDateTime.now(),
            new UrlOficial("https://x.org"), Convocatoria.EstadoConvocatoria.ABIERTA,
            List.of(), List.of(), null
        );
        ConvocatoriaEntity e = mapper.toEntity(d);
        assertThat(e.getMonto()).isNull();
        assertThat(e.getMoneda()).isNull();
    }
}