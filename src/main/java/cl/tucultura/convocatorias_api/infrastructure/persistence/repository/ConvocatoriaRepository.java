package cl.tucultura.convocatorias_api.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.tucultura.convocatorias_api.infrastructure.persistence.entity.ConvocatoriaEntity;

@Repository
public interface ConvocatoriaRepository extends JpaRepository<ConvocatoriaEntity, UUID> {
    List<ConvocatoriaEntity> findByEstado(String estado);
    List<ConvocatoriaEntity> findByEstadoAndFechaCierreAfter(String estado, LocalDateTime fechaCierre);
    List<ConvocatoriaEntity> findByCategoriaContainingIgnoreCase(String categoria);
    List<ConvocatoriaEntity> findByEstadoAndCategoriaContainingIgnoreCase(String estado, String categoria);
}
