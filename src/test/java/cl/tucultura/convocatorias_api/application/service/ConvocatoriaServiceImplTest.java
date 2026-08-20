package cl.tucultura.convocatorias_api.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.tucultura.convocatorias_api.domain.model.Convocatoria;
import cl.tucultura.convocatorias_api.infrastructure.persistence.entity.ConvocatoriaEntity;
import cl.tucultura.convocatorias_api.infrastructure.persistence.mapper.ConvocatoriaMapper;
import cl.tucultura.convocatorias_api.infrastructure.persistence.repository.ConvocatoriaRepository;

@ExtendWith(MockitoExtension.class)
class ConvocatoriaServiceImplTest {

    @Mock
    private ConvocatoriaRepository repository;

    @Mock
    private ConvocatoriaMapper mapper;

    @InjectMocks
    private ConvocatoriaServiceImpl service;

    private Convocatoria crearDominio(LocalDateTime apertura, LocalDateTime cierre, Convocatoria.EstadoConvocatoria estado) {
        return new Convocatoria(
            UUID.randomUUID(), "titulo", "descripcion",
            Convocatoria.TipoConvocatoria.BECA, "Música",
            BigDecimal.valueOf(1000000), "CLP",
            apertura, cierre, "https://ejemplo.org",
            estado, List.of(), List.of(), null
        );
    }

    @Test
    void listarActivas_devuelveListaDeConvocatorias() {
        ConvocatoriaEntity entity = new ConvocatoriaEntity();
        Convocatoria dominio = crearDominio(
            LocalDateTime.now().minusDays(10), LocalDateTime.now().plusDays(10),
            Convocatoria.EstadoConvocatoria.ABIERTA
        );

        when(repository.findByEstadoAndFechaCierreAfter(eq("ABIERTA"), any(LocalDateTime.class)))
            .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(dominio);

        List<Convocatoria> resultado = service.listarActivas();

        assertEquals(1, resultado.size());
        assertEquals("titulo", resultado.get(0).titulo());
    }

    @Test
    void listarActivas_listaVacia_devuelveListaVacia() {
        when(repository.findByEstadoAndFechaCierreAfter(eq("ABIERTA"), any(LocalDateTime.class)))
            .thenReturn(List.of());

        List<Convocatoria> resultado = service.listarActivas();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorId_existente_devuelveConvocatoria() {
        UUID id = UUID.randomUUID();
        ConvocatoriaEntity entity = new ConvocatoriaEntity();
        Convocatoria dominio = crearDominio(
            LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(5),
            Convocatoria.EstadoConvocatoria.ABIERTA
        );

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(dominio);

        Optional<Convocatoria> resultado = service.obtenerPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals("titulo", resultado.get().titulo());
    }

    @Test
    void obtenerPorId_noExistente_devuelveOptionalVacio() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Convocatoria> resultado = service.obtenerPorId(id);

        assertFalse(resultado.isPresent());
    }

    @Test
    void crearConvocatoria_fechaEnRango_estadoAbierta() {
        LocalDateTime apertura = LocalDateTime.now().minusDays(10);
        LocalDateTime cierre = LocalDateTime.now().plusDays(10);
        Convocatoria entrada = crearDominio(apertura, cierre, null);
        ConvocatoriaEntity entityGuardada = new ConvocatoriaEntity();
        Convocatoria resultado = crearDominio(apertura, cierre, Convocatoria.EstadoConvocatoria.ABIERTA);

        when(mapper.toEntity(any())).thenReturn(entityGuardada);
        when(repository.save(entityGuardada)).thenReturn(entityGuardada);
        when(mapper.toDomain(entityGuardada)).thenReturn(resultado);

        Convocatoria creada = service.crearConvocatoria(entrada);

        assertEquals(Convocatoria.EstadoConvocatoria.ABIERTA, creada.estado());
        verify(mapper).toEntity(argThat(c -> c.estado() == Convocatoria.EstadoConvocatoria.ABIERTA));
    }

    @Test
    void crearConvocatoria_fechaAperturaFutura_estadoProximamente() {
        LocalDateTime apertura = LocalDateTime.now().plusDays(10);
        LocalDateTime cierre = LocalDateTime.now().plusDays(20);
        Convocatoria entrada = crearDominio(apertura, cierre, null);
        ConvocatoriaEntity entityGuardada = new ConvocatoriaEntity();
        Convocatoria resultado = crearDominio(apertura, cierre, Convocatoria.EstadoConvocatoria.PROXIMAMENTE);

        when(mapper.toEntity(any())).thenReturn(entityGuardada);
        when(repository.save(entityGuardada)).thenReturn(entityGuardada);
        when(mapper.toDomain(entityGuardada)).thenReturn(resultado);

        Convocatoria creada = service.crearConvocatoria(entrada);

        assertEquals(Convocatoria.EstadoConvocatoria.PROXIMAMENTE, creada.estado());
    }

    @Test
    void crearConvocatoria_fechaCierrePasado_estadoCerrada() {
        LocalDateTime apertura = LocalDateTime.now().minusDays(20);
        LocalDateTime cierre = LocalDateTime.now().minusDays(10);
        Convocatoria entrada = crearDominio(apertura, cierre, null);
        ConvocatoriaEntity entityGuardada = new ConvocatoriaEntity();
        Convocatoria resultado = crearDominio(apertura, cierre, Convocatoria.EstadoConvocatoria.CERRADA);

        when(mapper.toEntity(any())).thenReturn(entityGuardada);
        when(repository.save(entityGuardada)).thenReturn(entityGuardada);
        when(mapper.toDomain(entityGuardada)).thenReturn(resultado);

        Convocatoria creada = service.crearConvocatoria(entrada);

        assertEquals(Convocatoria.EstadoConvocatoria.CERRADA, creada.estado());
    }

    @Test
    void crearConvocatoria_guardaEntidadMapeada() {
        LocalDateTime apertura = LocalDateTime.now().minusDays(5);
        LocalDateTime cierre = LocalDateTime.now().plusDays(5);
        Convocatoria entrada = crearDominio(apertura, cierre, null);
        ConvocatoriaEntity entityGuardada = new ConvocatoriaEntity();
        Convocatoria resultado = crearDominio(apertura, cierre, Convocatoria.EstadoConvocatoria.ABIERTA);

        when(mapper.toEntity(any())).thenReturn(entityGuardada);
        when(repository.save(entityGuardada)).thenReturn(entityGuardada);
        when(mapper.toDomain(entityGuardada)).thenReturn(resultado);

        service.crearConvocatoria(entrada);

        verify(mapper).toEntity(any());
        verify(repository).save(entityGuardada);
    }

    @Test
    void buscarPorFiltros_ambosParametros_devuelveFiltrado() {
        ConvocatoriaEntity entity = new ConvocatoriaEntity();
        Convocatoria dominio = crearDominio(
            LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(5),
            Convocatoria.EstadoConvocatoria.ABIERTA
        );

        when(repository.findByEstadoAndCategoriaContainingIgnoreCase("ABIERTA", "Música"))
            .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(dominio);

        List<Convocatoria> resultado = service.buscarPorFiltros("ABIERTA", "Música");

        assertEquals(1, resultado.size());
        assertEquals("Música", resultado.get(0).categoria());
    }

    @Test
    void buscarPorFiltros_soloEstadoAbierta_filtraPorFechaCierre() {
        ConvocatoriaEntity entity = new ConvocatoriaEntity();
        Convocatoria dominio = crearDominio(
            LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(5),
            Convocatoria.EstadoConvocatoria.ABIERTA
        );

        when(repository.findByEstadoAndFechaCierreAfter(eq("ABIERTA"), any(LocalDateTime.class)))
            .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(dominio);

        List<Convocatoria> resultado = service.buscarPorFiltros("ABIERTA", null);

        assertEquals(1, resultado.size());
        verify(repository).findByEstadoAndFechaCierreAfter(eq("ABIERTA"), any(LocalDateTime.class));
    }

    @Test
    void buscarPorFiltros_soloCategoria_devuelvePorCategoria() {
        ConvocatoriaEntity entity = new ConvocatoriaEntity();
        Convocatoria dominio = crearDominio(
            LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(5),
            Convocatoria.EstadoConvocatoria.ABIERTA
        );

        when(repository.findByCategoriaContainingIgnoreCase("Música"))
            .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(dominio);

        List<Convocatoria> resultado = service.buscarPorFiltros(null, "Música");

        assertEquals(1, resultado.size());
        verify(repository).findByCategoriaContainingIgnoreCase("Música");
    }

    @Test
    void buscarPorFiltros_sinParametros_devuelveActivas() {
        ConvocatoriaEntity entity = new ConvocatoriaEntity();
        Convocatoria dominio = crearDominio(
            LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(5),
            Convocatoria.EstadoConvocatoria.ABIERTA
        );

        when(repository.findByEstadoAndFechaCierreAfter(eq("ABIERTA"), any(LocalDateTime.class)))
            .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(dominio);

        List<Convocatoria> resultado = service.buscarPorFiltros(null, null);

        assertEquals(1, resultado.size());
        verify(repository).findByEstadoAndFechaCierreAfter(eq("ABIERTA"), any(LocalDateTime.class));
    }
}
