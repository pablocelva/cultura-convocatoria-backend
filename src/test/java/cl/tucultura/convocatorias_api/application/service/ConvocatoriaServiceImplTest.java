package cl.tucultura.convocatorias_api.application.service;

import static org.assertj.core.api.Assertions.assertThat;
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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.tucultura.convocatorias_api.domain.model.Convocatoria;
import cl.tucultura.convocatorias_api.domain.valueobject.*;
import cl.tucultura.convocatorias_api.infrastructure.persistence.entity.ConvocatoriaEntity;
import cl.tucultura.convocatorias_api.infrastructure.persistence.mapper.ConvocatoriaMapper;
import cl.tucultura.convocatorias_api.infrastructure.persistence.repository.ConvocatoriaRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Convocatoria Service Implementation")
class ConvocatoriaServiceImplTest {

    @Mock
    private ConvocatoriaRepository repository;

    @Mock
    private ConvocatoriaMapper mapper;

    @InjectMocks
    private ConvocatoriaServiceImpl service;

    private Convocatoria crearDominio(LocalDateTime apertura, LocalDateTime cierre, Convocatoria.EstadoConvocatoria estado) {
        return new Convocatoria(
            UUID.randomUUID(), new Titulo("titulo"), new Descripcion("descripcion"),
            Convocatoria.TipoConvocatoria.BECA, new Categoria("Música"),
            new Monto(BigDecimal.valueOf(1000000), "CLP"),
            apertura, cierre, new UrlOficial("https://ejemplo.org"),
            estado, List.of(), List.of(), null
        );
    }

    @Test
    @DisplayName("Should return list of active convocatorias")
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

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).titulo().value()).isEqualTo("titulo");
    }

    @Test
    @DisplayName("Should return empty list when no active convocatorias")
    void listarActivas_listaVacia_devuelveListaVacia() {
        when(repository.findByEstadoAndFechaCierreAfter(eq("ABIERTA"), any(LocalDateTime.class)))
            .thenReturn(List.of());

        List<Convocatoria> resultado = service.listarActivas();

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Should return convocatoria when found by id")
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

        assertThat(resultado).isPresent();
        assertThat(resultado.get().titulo().value()).isEqualTo("titulo");
    }

    @Test
    @DisplayName("Should return empty optional when convocatoria not found")
    void obtenerPorId_noExistente_devuelveOptionalVacio() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Convocatoria> resultado = service.obtenerPorId(id);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Should set estado ABIERTA when dates are within range")
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

        assertThat(creada.estado()).isEqualTo(Convocatoria.EstadoConvocatoria.ABIERTA);
        verify(mapper).toEntity(argThat(c -> c.estado() == Convocatoria.EstadoConvocatoria.ABIERTA));
    }

    @Test
    @DisplayName("Should set estado PROXIMAMENTE when opening date is in the future")
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

        assertThat(creada.estado()).isEqualTo(Convocatoria.EstadoConvocatoria.PROXIMAMENTE);
    }

    @Test
    @DisplayName("Should set estado CERRADA when closing date is in the past")
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

        assertThat(creada.estado()).isEqualTo(Convocatoria.EstadoConvocatoria.CERRADA);
    }

    @Test
    @DisplayName("Should save mapped entity when creating convocatoria")
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
    @DisplayName("Should filter by estado and categoria when both provided")
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

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).categoria().value()).isEqualTo("Música");
    }

    @Test
    @DisplayName("Should filter by estado only with fecha check when estado is ABIERTA")
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

        assertThat(resultado).hasSize(1);
        verify(repository).findByEstadoAndFechaCierreAfter(eq("ABIERTA"), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should filter by categoria only")
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

        assertThat(resultado).hasSize(1);
        verify(repository).findByCategoriaContainingIgnoreCase("Música");
    }

    @Test
    @DisplayName("Should return active convocatorias when no filters provided")
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

        assertThat(resultado).hasSize(1);
        verify(repository).findByEstadoAndFechaCierreAfter(eq("ABIERTA"), any(LocalDateTime.class));
    }
}