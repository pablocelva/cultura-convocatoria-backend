package cl.tucultura.convocatorias_api.infrastructure.persistence.mapper;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import cl.tucultura.convocatorias_api.domain.model.Convocatoria;
import cl.tucultura.convocatorias_api.domain.valueobject.*;
import cl.tucultura.convocatorias_api.infrastructure.persistence.entity.ConvocatoriaEntity;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.JacksonException;

@Component
public class ConvocatoriaMapper {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Convocatoria toDomain(ConvocatoriaEntity entity) {
        if (entity == null) return null;
        return new Convocatoria(
            entity.getId(),
            new Titulo(entity.getTitulo()),
            new Descripcion(entity.getDescripcion()),
            Convocatoria.TipoConvocatoria.valueOf(entity.getTipo()),
            new Categoria(entity.getCategoria()),
            entity.getMonto() != null ? new Monto(entity.getMonto(), entity.getMoneda()) : null,
            entity.getFechaApertura(),
            entity.getFechaCierre(),
            new UrlOficial(entity.getUrlOficial()),
            Convocatoria.EstadoConvocatoria.valueOf(entity.getEstado()),
            parseJson(entity.getRequisitosJson()),
            parseJson(entity.getDocumentacionJson()),
            entity.getFuenteId()
        );
    }

    public ConvocatoriaEntity toEntity(Convocatoria convocatoria) {
        if (convocatoria == null) return null;
        ConvocatoriaEntity entity = new ConvocatoriaEntity();
        entity.setId(convocatoria.id() != null ? convocatoria.id() : UUID.randomUUID());
        entity.setTitulo(convocatoria.titulo().value());
        entity.setDescripcion(convocatoria.descripcion().value());
        entity.setTipo(convocatoria.tipo().name());
        entity.setCategoria(convocatoria.categoria().value());
        entity.setMonto(convocatoria.monto() != null ? convocatoria.monto().value() : null);
        entity.setMoneda(convocatoria.monto() != null ? convocatoria.monto().moneda() : null);
        entity.setFechaApertura(convocatoria.fechaApertura());
        entity.setFechaCierre(convocatoria.fechaCierre());
        entity.setUrlOficial(convocatoria.urlOficial().value());
        entity.setEstado(convocatoria.estado().name());
        entity.setFuenteId(convocatoria.fuenteId());
        entity.setRequisitosJson(toJson(convocatoria.requisitos()));
        entity.setDocumentacionJson(toJson(convocatoria.documentacion()));
        return entity;
    }

    private List<String> parseJson(String json) {
        if (json == null || json.isEmpty()) return List.of();
        try { return objectMapper.readValue(json, new TypeReference<List<String>>() {}); }
        catch (JacksonException e) { return List.of(); }
    }

    private String toJson(List<String> list) {
        if (list ==null) return "[]";
        try { return objectMapper.writeValueAsString(list); }
        catch (JacksonException e) { return "[]"; }
    }
}
