package mensajeria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import mensajeria.api.IDirectorio;
import mensajeria.api.Nodo;

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
        return entradas.get(nombre);
    }

    @Override
    public List<String> nombres() {
        return new ArrayList<>(entradas.keySet());
    }
}
