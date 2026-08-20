package cl.tucultura.convocatorias_api.domain.valueobject;

public record Descripcion(String value) {
    public Descripcion {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }
        value = value.trim();
    }
}
