package modelo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//acomoda los paneles de los jugadores visualmente según el border layout
public enum Ubicacion {
    NORTE, SUR, ESTE, OESTE;

    //reparte ubicaciones, el jugador del pov siempre al sur, los demás según orden d eturnos
    public static Map<Integer, Ubicacion> distribuir(List<DatosJugador> jugadores, int idLocal) {
        int n = jugadores.size();
        List<Ubicacion> orden = switch (n) {
            case 2 -> List.of(SUR, NORTE);
            case 3 -> List.of(SUR, OESTE, ESTE);
            case 4 -> List.of(SUR, OESTE, NORTE, ESTE);
            default -> throw new IllegalArgumentException("Solo se soportan de 2 a 4 jugadores, llegaron " + n);
        };

        int indiceLocal = 0;
        for (int i = 0; i < n; i++) {
            if (jugadores.get(i).id() == idLocal) {
                indiceLocal = i;
                break;
            }
        }

        Map<Integer, Ubicacion> mapa = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            DatosJugador j = jugadores.get((indiceLocal + i) % n);
            mapa.put(j.id(), orden.get(i));
        }
        return mapa;
    }
}