package modeloJuego.objetosNegocio;

import java.util.List;

public class Jugada {

    private final Jugador jugador;
    private final Linea linea;
    private final List<Cuadro> cuadrosCompletados;

    public Jugada(Jugador jugador, Linea linea, List<Cuadro> cuadrosCompletados) {
        this.jugador = jugador;
        this.linea = linea;
        this.cuadrosCompletados = List.copyOf(cuadrosCompletados);
    }

    public boolean cerroCuadro() {
        return !cuadrosCompletados.isEmpty();
    }

    public Jugador getJugador() {
        return jugador;
    }

    public Linea getLinea() {
        return linea;
    }

    public List<Cuadro> getCuadrosCompletados() {
        return cuadrosCompletados;
    }
}
