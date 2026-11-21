package mensajeria;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Directorio implements IDirectorio {

    private final Map<String, Nodo> entradas = new ConcurrentHashMap<>();

    @Override
    public void registrar(String nombre, Nodo nodo) {
        entradas.put(nombre, nodo);
    }

    @Override
    public void eliminar(String nombre) {
        entradas.remove(nombre);
    }

    @Override
    public Nodo buscar(String nombre) {
        Nodo nodo = nombre == null ? null : entradas.get(nombre);
        if (nodo == null) {
            throw new DestinoNoEncontradoException(nombre);
        }
        return nodo;
    }

    @Override
    public List<String> nombres() {
        return List.copyOf(entradas.keySet());
    }
}
