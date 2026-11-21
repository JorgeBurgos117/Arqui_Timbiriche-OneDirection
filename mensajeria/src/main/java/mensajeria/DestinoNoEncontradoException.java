package mensajeria;

public class DestinoNoEncontradoException extends RuntimeException {

    public DestinoNoEncontradoException(String nombre) {
        super("No hay buzón registrado con el nombre: " + nombre);
    }
}
