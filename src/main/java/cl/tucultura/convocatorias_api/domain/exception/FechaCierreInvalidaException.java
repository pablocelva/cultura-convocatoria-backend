package cl.tucultura.convocatorias_api.domain.exception;

public class FechaCierreInvalidaException extends RuntimeException {
    public FechaCierreInvalidaException(String mensaje) {
        super(mensaje);
    }
}