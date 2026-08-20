package cl.tucultura.convocatorias_api.domain.exception;

public class EstadoInvalidoException extends RuntimeException {
    public EstadoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
