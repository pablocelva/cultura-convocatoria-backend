package cl.tucultura.convocatorias_api.domain.valueobject;

public record Categoria(String value) {
    public Categoria {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }
        value = value.trim();
    }
}
