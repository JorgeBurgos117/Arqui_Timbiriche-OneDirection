package vista.util;

import java.util.List;

/** Dónde se sienta cada jugador alrededor del tablero. Es solo acomodo visual. */
public enum Ubicacion {
    NORTE, SUR, ESTE, OESTE;

    /** El primero de la lista (el jugador local) siempre queda abajo (SUR). */
    public static List<Ubicacion> paraJugadores(int cantidad) {
        return switch (cantidad) {
            case 2 -> List.of(SUR, NORTE);
            case 3 -> List.of(SUR, OESTE, ESTE);
            default -> List.of(SUR, OESTE, NORTE, ESTE);
        };
    }
}
