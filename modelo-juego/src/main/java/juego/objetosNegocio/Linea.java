package juego.objetosNegocio;

import java.util.Objects;

public final class Linea {

    private final Punto punto1;
    private final Punto punto2;
    private final Posicion posicion;
    private final Jugador jugador;

    /**
     * Los puntos se ordenan al construir, así Linea(A,B) y Linea(B,A)
     * son la misma línea. La igualdad ignora al jugador.
     */
    public Linea(Punto p1, Punto p2, Jugador jugador) {
        if (!p1.esAdyacente(p2)) {
            throw new IllegalArgumentException("Los puntos no son adyacentes: " + p1 + " y " + p2);
        }
        boolean p1Primero = p1.getFila() < p2.getFila()
                || (p1.getFila() == p2.getFila() && p1.getColumna() < p2.getColumna());
        this.punto1 = p1Primero ? p1 : p2;
        this.punto2 = p1Primero ? p2 : p1;
        this.posicion = (punto1.getFila() == punto2.getFila())
                ? Posicion.HORIZONTAL
                : Posicion.VERTICAL;
        this.jugador = jugador;
    }

    public Punto getPunto1() {
        return punto1;
    }

    public Punto getPunto2() {
        return punto2;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public Jugador getJugador() {
        return jugador;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Linea otra)) return false;
        return punto1.equals(otra.punto1) && punto2.equals(otra.punto2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(punto1, punto2);
    }

    @Override
    public String toString() {
        return punto1 + "-" + punto2;
    }
}
