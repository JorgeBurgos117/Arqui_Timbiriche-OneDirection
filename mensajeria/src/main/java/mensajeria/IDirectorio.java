package mensajeria;

import java.util.List;

public interface IDirectorio {

    void registrar(String nombre, Nodo nodo);

    void eliminar(String nombre);

    Nodo buscar(String nombre) throws DestinoNoEncontradoException;

    List<String> nombres();
}
