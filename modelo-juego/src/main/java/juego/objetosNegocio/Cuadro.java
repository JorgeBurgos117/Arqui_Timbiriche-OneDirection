package juego.objetosNegocio;

public class Cuadro {

    // Esquina superior izquierda del cuadro
    private final int fila;
    private final int columna;

    private Linea lineaArriba;
    private Linea lineaAbajo;
    private Linea lineaIzquierda;
    private Linea lineaDerecha;

    private EstadoCuadro estado = EstadoCuadro.INCOMPLETO;
    private Jugador jugador;

    public Cuadro(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public boolean esLadoDe(Linea linea) {
        return ladoDe(linea) != null;
    }

    public void agregarLinea(Linea linea) {
        Lado lado = ladoDe(linea);
        if (lado == null) {
            return;
        }
        switch (lado) {
            case ARRIBA -> lineaArriba = linea;
            case ABAJO -> lineaAbajo = linea;
            case IZQUIERDA -> lineaIzquierda = linea;
            case DERECHA -> lineaDerecha = linea;
        }
    }

    public boolean estaCompleto() {
        return lineaArriba != null && lineaAbajo != null
                && lineaIzquierda != null && lineaDerecha != null;
    }

    public void reclamar(Jugador jugador) {
        if (estado == EstadoCuadro.RECLAMADO) {
            return;
        }
        this.jugador = jugador;
        this.estado = EstadoCuadro.RECLAMADO;
    }

    public EstadoCuadro getEstado() {
        return estado;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    private enum Lado { ARRIBA, ABAJO, IZQUIERDA, DERECHA }

    private Lado ladoDe(Linea linea) {
        Punto a = linea.getPunto1();
        Punto b = linea.getPunto2();
        if (linea.getPosicion() == Posicion.HORIZONTAL) {
            if (a.getColumna() != columna || b.getColumna() != columna + 1) {
                return null;
            }
            if (a.getFila() == fila) return Lado.ARRIBA;
            if (a.getFila() == fila + 1) return Lado.ABAJO;
            return null;
        }
        if (a.getFila() != fila || b.getFila() != fila + 1) {
            return null;
        }
        if (a.getColumna() == columna) return Lado.IZQUIERDA;
        if (a.getColumna() == columna + 1) return Lado.DERECHA;
        return null;
    }
}
