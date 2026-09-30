package modelo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum Ubicacion {
    NORTE, SUR, ESTE, OESTE;

    public static Map<Integer, Ubicacion> distribuir(List<Integer> idsEnOrdenDeTurno, int idLocal) {
        int n = idsEnOrdenDeTurno.size();
        List<Ubicacion> orden = switch (n) {
            case 2 -> List.of(SUR, NORTE);
            case 3 -> List.of(SUR, OESTE, ESTE);
            case 4 -> List.of(SUR, OESTE, NORTE, ESTE);
            default -> throw new IllegalArgumentException("Solo se soportan de 2 a 4 jugadores, llegaron " + n);
        };

        int indiceLocal = Math.max(0, idsEnOrdenDeTurno.indexOf(idLocal));
        Map<Integer, Ubicacion> mapa = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            mapa.put(idsEnOrdenDeTurno.get((indiceLocal + i) % n), orden.get(i));
        }
        return mapa;
    }
}
