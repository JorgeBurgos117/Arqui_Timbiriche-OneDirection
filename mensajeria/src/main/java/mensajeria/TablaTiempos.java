package mensajeria;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TablaTiempos {

    private final Map<String, LocalDateTime> tiempos = new ConcurrentHashMap<>();

    public void actualizar(String nombre) {
        tiempos.put(nombre, LocalDateTime.now());
    }

    public List<String> vencidos(Duration tiempoVida) {
        LocalDateTime limite = LocalDateTime.now().minus(tiempoVida);
        return tiempos.entrySet().stream()
                .filter(e -> e.getValue().isBefore(limite))
                .map(Map.Entry::getKey)
                .toList();
    }

    public void eliminar(String nombre) {
        tiempos.remove(nombre);
    }
}
