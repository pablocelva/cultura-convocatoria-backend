package cl.tucultura.convocatorias_api.domain.valueobject;

public record Titulo(String value) {
    public Titulo {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
        if (value.trim().length() > 255) {
            throw new IllegalArgumentException("El título no puede superar los 255 caracteres");
        }
        value = value.trim();
    }
}
