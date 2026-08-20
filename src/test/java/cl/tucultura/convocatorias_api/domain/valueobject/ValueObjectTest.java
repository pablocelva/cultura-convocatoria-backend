package cl.tucultura.convocatorias_api.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;

@DisplayName("Value Objects Validation")
class ValueObjectTest {

    @Nested
    @DisplayName("Titulo")
    class TituloTest {

        @ParameterizedTest(name = "\"{0}\" -> debería crear título válido")
        @ValueSource(strings = {"Beca Música", "Fondo de Arte", "a", "   Beca con espacios   "})
        @DisplayName("Should create Titulo with valid values")
        void tituloValido_creaTitulo(String valor) {
            Titulo titulo = new Titulo(valor);
            assertThat(titulo.value()).isNotBlank();
            assertThat(titulo.value().length()).isLessThanOrEqualTo(255);
        }

        @ParameterizedTest(name = "\"{0}\" -> debería lanzar excepción")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should throw exception when value is null, blank or whitespace")
        void tituloInvalido_lanzaExcepcion(String valor) {
            assertThatThrownBy(() -> new Titulo(valor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El título es obligatorio");
        }

        @Test
        @DisplayName("Should trim whitespace from value")
        void tituloConEspacios_seDeberiaRecortar() {
            Titulo titulo = new Titulo("  Beca Música  ");
            assertThat(titulo.value()).isEqualTo("Beca Música");
        }

        @Test
        @DisplayName("Should throw exception when value exceeds 255 characters")
        void tituloMayorA255_lanzaExcepcion() {
            String largo = "a".repeat(256);
            assertThatThrownBy(() -> new Titulo(largo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El título no puede superar los 255 caracteres");
        }
    }

    @Nested
    @DisplayName("Descripcion")
    class DescripcionTest {

        @ParameterizedTest(name = "\"{0}\" -> debería crear descripción válida")
        @ValueSource(strings = {"Descripción corta", "Una descripción más larga con detalles", "a"})
        @DisplayName("Should create Descripcion with valid values")
        void descripcionValida_creaDescripcion(String valor) {
            Descripcion desc = new Descripcion(valor);
            assertThat(desc.value()).isNotBlank();
        }

        @ParameterizedTest(name = "\"{0}\" -> debería lanzar excepción")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("Should throw exception when value is null, blank or whitespace")
        void descripcionInvalida_lanzaExcepcion(String valor) {
            assertThatThrownBy(() -> new Descripcion(valor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La descripción es obligatoria");
        }
    }

    @Nested
    @DisplayName("Categoria")
    class CategoriaTest {

        @ParameterizedTest(name = "\"{0}\" -> debería crear categoría válida")
        @ValueSource(strings = {"Música", "Artes Visuales", "Danza", "a"})
        @DisplayName("Should create Categoria with valid values")
        void categoriaValida_creaCategoria(String valor) {
            Categoria cat = new Categoria(valor);
            assertThat(cat.value()).isNotBlank();
        }

        @ParameterizedTest(name = "\"{0}\" -> debería lanzar excepción")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("Should throw exception when value is null, blank or whitespace")
        void categoriaInvalida_lanzaExcepcion(String valor) {
            assertThatThrownBy(() -> new Categoria(valor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La categoría es obligatoria");
        }
    }

    @Nested
    @DisplayName("Monto")
    class MontoTest {

        static Stream<Arguments> montosValidos() {
            return Stream.of(
                Arguments.of(BigDecimal.ZERO, "CLP", BigDecimal.ZERO, "CLP"),
                Arguments.of(BigDecimal.valueOf(1000000), "USD", BigDecimal.valueOf(1000000), "USD"),
                Arguments.of(BigDecimal.valueOf(5000000), null, BigDecimal.valueOf(5000000), "CLP"),
                Arguments.of(BigDecimal.valueOf(500), "eur", BigDecimal.valueOf(500), "EUR")
            );
        }

        @ParameterizedTest(name = "monto={0}, moneda={1} -> valor={2}, moneda={3}")
        @MethodSource("montosValidos")
        @DisplayName("Should create Monto with valid values")
        void montoValido_creaMonto(BigDecimal entradaMoneda, String entradaMonedaStr, BigDecimal esperadoValor, String esperadoMoneda) {
            Monto monto = new Monto(entradaMoneda, entradaMonedaStr);
            assertThat(monto.value()).isEqualByComparingTo(esperadoValor);
            assertThat(monto.moneda()).isEqualTo(esperadoMoneda);
        }

        @ParameterizedTest(name = "monto={0} -> debería lanzar excepción")
        @CsvSource({
            "-1, CLP",
            "-0.01, USD",
            "-999999, EUR"
        })
        @DisplayName("Should throw exception when value is negative")
        void montoNegativo_lanzaExcepcion(BigDecimal valor, String moneda) {
            assertThatThrownBy(() -> new Monto(valor, moneda))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El monto no puede ser negativo");
        }

        @Test
        @DisplayName("Should default moneda to CLP when null")
        void montoNullMoneda_defaultClp() {
            Monto monto = new Monto(BigDecimal.valueOf(1000), null);
            assertThat(monto.moneda()).isEqualTo("CLP");
        }

        @Test
        @DisplayName("Should default moneda to CLP when blank")
        void montoMonedaEnBlanco_defaultClp() {
            Monto monto = new Monto(BigDecimal.valueOf(1000), "   ");
            assertThat(monto.moneda()).isEqualTo("CLP");
        }

        @Test
        @DisplayName("Should allow null value")
        void montoValorNull_permitido() {
            Monto monto = new Monto(null, "CLP");
            assertThat(monto.value()).isNull();
        }

        @Test
        @DisplayName("Factory method crear should work same as constructor")
        void crear_factoryMethodFunciona() {
            Monto monto = Monto.crear(BigDecimal.valueOf(5000), "USD");
            assertThat(monto.value()).isEqualByComparingTo(BigDecimal.valueOf(5000));
            assertThat(monto.moneda()).isEqualTo("USD");
        }
    }

    @Nested
    @DisplayName("UrlOficial")
    class UrlOficialTest {

        @ParameterizedTest(name = "\"{0}\" -> debería crear URL válida")
        @ValueSource(strings = {
            "https://cultura.gob.cl",
            "http://ejemplo.org",
            "https://www.fondos.cl/beca/2026",
            "https://sub.dominio.largo/ruta?param=1&otro=2"
        })
        @DisplayName("Should create UrlOficial with valid URLs")
        void urlValida_creaUrl(String valor) {
            UrlOficial url = new UrlOficial(valor);
            assertThat(url.value()).isEqualTo(valor);
        }

        @ParameterizedTest(name = "\"{0}\" -> debería lanzar excepción")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("Should throw exception when value is null, blank or whitespace")
        void urlInvalida_nulaVacia_lanzaExcepcion(String valor) {
            assertThatThrownBy(() -> new UrlOficial(valor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La URL oficial es obligatoria");
        }

        @ParameterizedTest(name = "\"{0}\" -> debería lanzar excepción de formato")
        @ValueSource(strings = {
            "ftp://ejemplo.org",
            "sin-protocolo.com",
            "ejemplo.org",
            "https://"
        })
        @DisplayName("Should throw exception when URL format is invalid")
        void urlFormatoInvalido_lanzaExcepcion(String valor) {
            assertThatThrownBy(() -> new UrlOficial(valor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La URL debe tener un formato válido");
        }
    }
}