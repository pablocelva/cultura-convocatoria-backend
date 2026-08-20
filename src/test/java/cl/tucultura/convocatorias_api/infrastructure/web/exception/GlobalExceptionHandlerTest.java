package cl.tucultura.convocatorias_api.infrastructure.web.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import cl.tucultura.convocatorias_api.domain.exception.EstadoInvalidoException;
import cl.tucultura.convocatorias_api.domain.exception.FechaCierreInvalidaException;

@DisplayName("Global Exception Handler")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should return 400 with message when IllegalArgumentException is thrown")
    void illegalArgument_retorna400ConMensaje() {
        ResponseEntity<Map<String, Object>> response = 
            handler.handleIllegalArgument(new IllegalArgumentException("Campo inválido"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(response.getBody().get("message")).isEqualTo("Campo inválido");
        assertThat(response.getBody().get("timestamp")).isNotNull();
    }

    @Test
    @DisplayName("Should return 400 with message when FechaCierreInvalidaException is thrown")
    void fechaCierreInvalida_retorna400ConMensaje() {
        ResponseEntity<Map<String, Object>> response = 
            handler.handleFechaCierreInvalida(new FechaCierreInvalidaException("Fecha inválida"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(response.getBody().get("message")).isEqualTo("Fecha inválida");
    }

    @Test
    @DisplayName("Should return 400 with message when EstadoInvalidoException is thrown")
    void estadoInvalido_retorna400ConMensaje() {
        ResponseEntity<Map<String, Object>> response = 
            handler.handleEstadoInvalido(new EstadoInvalidoException("Estado no válido"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(response.getBody().get("message")).isEqualTo("Estado no válido");
    }

    @Test
    @DisplayName("Should return 500 when generic Exception is thrown")
    void exceptionGenerica_retorna500() {
        ResponseEntity<Map<String, Object>> response = 
            handler.handleGenericException(new Exception("Error inesperado"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(500);
        assertThat(response.getBody().get("message")).isEqualTo("Ocurrió un error inesperado en el servidor.");    }
}