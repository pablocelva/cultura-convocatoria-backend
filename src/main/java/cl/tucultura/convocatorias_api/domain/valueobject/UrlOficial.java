package cl.tucultura.convocatorias_api.domain.valueobject;

public record UrlOficial(String value) {
    public UrlOficial {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("La URL oficial es obligatoria");
        }
        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            throw new IllegalArgumentException("La URL debe tener un formato válido");
        }
        if (value.startsWith("http://") && value.length() <= "http://".length()) {
            throw new IllegalArgumentException("La URL debe tener un formato válido");
        }
        if (value.startsWith("https://") && value.length() <= "https://".length()) {
            throw new IllegalArgumentException("La URL debe tener un formato válido");
        }
    }
}
