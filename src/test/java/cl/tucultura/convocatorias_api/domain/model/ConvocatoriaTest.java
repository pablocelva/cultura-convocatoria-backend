package cl.tucultura.convocatorias_api.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.tucultura.convocatorias_api.domain.valueobject.*;

@DisplayName("Convocatoria Domain Model")
class ConvocatoriaTest {

    private Convocatoria crearConvocatoria(LocalDateTime apertura, LocalDateTime cierre) {
        return new Convocatoria(
            UUID.randomUUID(), new Titulo("titulo"), new Descripcion("descripcion"),
            Convocatoria.TipoConvocatoria.BECA, new Categoria("Música"),
            new Monto(BigDecimal.valueOf(1000000), "CLP"),
            apertura, cierre,
            new UrlOficial("https://ejemplo.org"), Convocatoria.EstadoConvocatoria.ABIERTA,
            List.of(), List.of(), null
        );
    }

    @Test
    @DisplayName("Should return true when current date is within opening and closing dates")
    void estavigente_fechasEnRango_devuelveTrue() {
        Convocatoria c = crearConvocatoria(
            LocalDateTime.now().minusDays(10),
            LocalDateTime.now().plusDays(10)
        );
        assertThat(c.estavigente()).isTrue();
    }

    @Test
    @DisplayName("Should return false when opening date is in the future")
    void estavigente_fechaFutura_devuelveFalse() {
        Convocatoria c = crearConvocatoria(
            LocalDateTime.now().plusDays(10),
            LocalDateTime.now().plusDays(20)
        );
        assertThat(c.estavigente()).isFalse();
    }

    @Test
    @DisplayName("Should return false when closing date is in the past")
    void estavigente_fechaPasado_devuelveFalse() {
        Convocatoria c = crearConvocatoria(
            LocalDateTime.now().minusDays(20),
            LocalDateTime.now().minusDays(10)
        );
        assertThat(c.estavigente()).isFalse();
    }
}