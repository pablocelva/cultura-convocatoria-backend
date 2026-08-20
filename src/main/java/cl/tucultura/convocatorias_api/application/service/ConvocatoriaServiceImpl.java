package cl.tucultura.convocatorias_api.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.tucultura.convocatorias_api.domain.model.Convocatoria;
import cl.tucultura.convocatorias_api.infrastructure.persistence.entity.ConvocatoriaEntity;
import cl.tucultura.convocatorias_api.infrastructure.persistence.mapper.ConvocatoriaMapper;
import cl.tucultura.convocatorias_api.infrastructure.persistence.repository.ConvocatoriaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConvocatoriaServiceImpl implements ConvocatoriaService {
    
    private final ConvocatoriaRepository repository;
    private final ConvocatoriaMapper mapper;

    @Override
    public List<Convocatoria> listarActivas() {
        List<ConvocatoriaEntity> entities = repository.findByEstadoAndFechaCierreAfter("ABIERTA", LocalDateTime.now());
        return entities.stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Convocatoria> obtenerPorId(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Convocatoria> buscarPorFiltros(String estado, String categoria) {
        List<ConvocatoriaEntity> entities;

        if (estado != null && categoria != null) {
            entities = repository.findByEstadoAndCategoriaContainingIgnoreCase(estado, categoria);
        } else if (estado != null) {
            if (Convocatoria.EstadoConvocatoria.valueOf(estado) == Convocatoria.EstadoConvocatoria.ABIERTA) {
                entities = repository.findByEstadoAndFechaCierreAfter(estado, LocalDateTime.now());
            } else {
                entities = repository.findByEstado(estado);
            }
        } else if (categoria != null) {
            entities = repository.findByCategoriaContainingIgnoreCase(categoria);
        } else {
            return listarActivas();
        }

        return entities.stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Convocatoria crearConvocatoria(Convocatoria convocatoria) {
        LocalDateTime ahora = LocalDateTime.now();
        Convocatoria.EstadoConvocatoria estadoCalculado;

        if (convocatoria.fechaCierre().isBefore(ahora)) estadoCalculado = Convocatoria.EstadoConvocatoria.CERRADA;
        else if (convocatoria.fechaApertura().isAfter(ahora)) estadoCalculado = Convocatoria.EstadoConvocatoria.PROXIMAMENTE;
        else estadoCalculado = Convocatoria.EstadoConvocatoria.ABIERTA;

        Convocatoria convocatoriaConEstado = new Convocatoria(
            convocatoria.id(), convocatoria.titulo(), convocatoria.descripcion(),
            convocatoria.tipo(), convocatoria.categoria(), convocatoria.monto(),
            convocatoria.moneda(), convocatoria.fechaApertura(), convocatoria.fechaCierre(),
            convocatoria.urlOficial(), estadoCalculado, convocatoria.requisitos(),
            convocatoria.documentacion(), convocatoria.fuenteId()
        );

        ConvocatoriaEntity entity = mapper.toEntity(convocatoriaConEstado);
        return mapper.toDomain(repository.save(entity));
    }
}
