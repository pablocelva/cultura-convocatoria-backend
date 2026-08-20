package cl.tucultura.convocatorias_api.infrastructure.web.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @SuppressWarnings("unchecked")
    void handleValidationExceptions_retorna400ConDetallesPorCampo() {
        FieldError fieldError = new FieldError("request", "titulo", "El título es obligatorio");
        BindingResult bindingResult = new org.springframework.validation.BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(fieldError);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
            (MethodParameter) null, bindingResult
        );

        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertNotNull(body.get("timestamp"));

        Map<String, String> details = (Map<String, String>) body.get("details");
        assertNotNull(details);
        assertEquals("El título es obligatorio", details.get("titulo"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void handleIllegalArgument_retorna400ConMensaje() {
        IllegalArgumentException ex = new IllegalArgumentException("La fecha de cierre debe ser posterior a la fecha de apertura.");

        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("La fecha de cierre debe ser posterior a la fecha de apertura.", body.get("message"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void handleGenericException_retorna500SinDetallesSensibles() {
        Exception ex = new RuntimeException("Error interno de la base de datos");

        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals("Ocurrió un error inesperado en el servidor.", body.get("message"));
        assertNull(body.get("debug"));
    }
}
