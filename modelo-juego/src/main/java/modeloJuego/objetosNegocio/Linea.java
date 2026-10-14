package modeloJuego.objetosNegocio;

public class Linea {

    private final Punto punto1;
    private final Punto punto2;
    private final Posicion posicion;
    private final Jugador jugador;

    public Linea(Punto a, Punto b, Jugador jugador) {
        boolean aEsMenor = a.getFila() < b.getFila() || (a.getFila() == b.getFila() && a.getColumna() < b.getColumna());
        this.punto1 = aEsMenor ? a : b;
        this.punto2 = aEsMenor ? b : a;
        this.posicion = a.getFila() == b.getFila() ? Posicion.HORIZONTAL : Posicion.VERTICAL;
        this.jugador = jugador;
    }

    public boolean une(Punto a, Punto b) {
        return (punto1.equals(a) && punto2.equals(b)) || (punto1.equals(b) && punto2.equals(a));
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
    public String toString() {
        return punto1 + "-" + punto2;
    }
}
