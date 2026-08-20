package cl.tucultura.convocatorias_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ConvocatoriaTest {

    private Convocatoria crearConvocatoria(LocalDateTime apertura, LocalDateTime cierre) {
        return new Convocatoria(
            UUID.randomUUID(), "titulo", "descripcion",
            Convocatoria.TipoConvocatoria.BECA, "Música",
            BigDecimal.valueOf(1000000), "CLP",
            apertura, cierre,
            "https://ejemplo.org", Convocatoria.EstadoConvocatoria.ABIERTA,
            List.of(), List.of(), null
        );
    }

    @Test
    void estavigente_fechasEnRango_devuelveTrue() {
        Convocatoria c = crearConvocatoria(
            LocalDateTime.now().minusDays(10),
            LocalDateTime.now().plusDays(10)
        );
        assertTrue(c.estavigente());
    }

    @Test
    void estavigente_fechaFutura_devuelveFalse() {
        Convocatoria c = crearConvocatoria(
            LocalDateTime.now().plusDays(10),
            LocalDateTime.now().plusDays(20)
        );
        assertFalse(c.estavigente());
    }

    @Test
    void estavigente_fechaPasado_devuelveFalse() {
        Convocatoria c = crearConvocatoria(
            LocalDateTime.now().minusDays(20),
            LocalDateTime.now().minusDays(10)
        );
        assertFalse(c.estavigente());
    }
}
