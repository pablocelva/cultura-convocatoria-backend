package cl.tucultura.convocatorias_api.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

    static Stream<Arguments> fechasEstavigente() {
        return Stream.of(
            Arguments.of(
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().plusDays(10),
                true,
                "fechas dentro del rango -> true"
            ),
            Arguments.of(
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(20),
                false,
                "apertura en el futuro -> false"
            ),
            Arguments.of(
                LocalDateTime.now().minusDays(20),
                LocalDateTime.now().minusDays(10),
                false,
                "cierre en el pasado -> false"
            )
        );
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("fechasEstavigente")
    @DisplayName("Should return correct estavigente value based on date range")
    void estavigente_fechasParametrizadas(LocalDateTime apertura, LocalDateTime cierre, boolean esperado, String desc) {
        Convocatoria c = crearConvocatoria(apertura, cierre);
        assertThat(c.estavigente()).isEqualTo(esperado);
    }
}