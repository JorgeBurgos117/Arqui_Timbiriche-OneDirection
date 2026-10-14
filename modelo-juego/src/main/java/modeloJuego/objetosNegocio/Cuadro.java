package modeloJuego.objetosNegocio;

public class Cuadro {

    private final int fila;
    private final int columna;
    private Linea lineaArriba;
    private Linea lineaAbajo;
    private Linea lineaIzquierda;
    private Linea lineaDerecha;
    private EstadoCuadro estado = EstadoCuadro.VACIO;
    private Jugador jugador;

    public Cuadro(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public void agregarLinea(Linea linea) {
        if (!esLadoDe(linea)) {
            return;
        }
        Punto inicio = linea.getPunto1();
        if (linea.getPosicion() == Posicion.HORIZONTAL) {
            if (inicio.getFila() == fila) {
                lineaArriba = linea;
            } else {
                lineaAbajo = linea;
            }
        } else if (inicio.getColumna() == columna) {
            lineaIzquierda = linea;
        } else {
            lineaDerecha = linea;
        }
        if (estado == EstadoCuadro.VACIO) {
            estado = EstadoCuadro.INCOMPLETO;
        }
    }

    public boolean esLadoDe(Linea linea) {
        Punto inicio = linea.getPunto1();
        if (linea.getPosicion() == Posicion.HORIZONTAL) {
            return inicio.getColumna() == columna && (inicio.getFila() == fila || inicio.getFila() == fila + 1);
        }
        return inicio.getFila() == fila && (inicio.getColumna() == columna || inicio.getColumna() == columna + 1);
    }

    public boolean estaCompleto() {
        return lineaArriba != null && lineaAbajo != null && lineaIzquierda != null && lineaDerecha != null;
    }

    public void reclamar(Jugador jugador) {
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
}
