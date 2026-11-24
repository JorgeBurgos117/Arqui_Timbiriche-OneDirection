package mensajeria.api;

import java.util.List;

public interface IDirectorio {
    void registrar(String nombre, Nodo nodo);
    void eliminar(String nombre);
    Nodo buscar(String nombre);
    List<String> nombres();
}
