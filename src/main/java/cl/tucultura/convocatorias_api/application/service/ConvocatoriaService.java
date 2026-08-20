package cl.tucultura.convocatorias_api.application.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import cl.tucultura.convocatorias_api.domain.model.Convocatoria;

public interface ConvocatoriaService {
    List<Convocatoria> listarActivas();
    Optional<Convocatoria> obtenerPorId(UUID id);
    List<Convocatoria> buscarPorFiltros(String estado, String categoria);
    Convocatoria crearConvocatoria(Convocatoria convocatoria);
}
