package juego.objetosNegocio;

import java.util.ArrayList;
import java.util.List;

public class Partida {

    private final Tablero tablero;
    private final Marcador marcador;
    private final List<Jugador> jugadores;
    private final List<Jugada> historial = new ArrayList<>();

    private Jugador jugadorActual;
    private EstadoPartida estado = EstadoPartida.EN_CURSO;
    private PuntoSeleccionado puntoSeleccionado;
    private String motivoRechazo;
    private String jugadorRechazado;

    public Partida(Tablero tablero, List<Jugador> jugadores) {
        if (jugadores.isEmpty()) {
            throw new IllegalArgumentException("La partida necesita al menos un jugador");
        }
        this.tablero = tablero;
        this.jugadores = List.copyOf(jugadores);
        this.marcador = new Marcador(this.jugadores);
        this.jugadorActual = this.jugadores.get(0);
    }


    public boolean seleccionarPunto(int fila, int columna, String nombreJugador) {
        if (!esTurnoDe(nombreJugador)) {
            return rechazar(nombreJugador, "No es el turno de " + nombreJugador);
        }
        Punto punto = tablero.getPunto(fila, columna);
        if (punto == null) {
            return rechazar(nombreJugador, "El punto está fuera del tablero");
        }

        if (puntoSeleccionado != null && puntoSeleccionado.getPunto().equals(punto)) {
            puntoSeleccionado = null; // mismo punto: se deselecciona
        } else {
            List<Punto> posibles = tablero.buscarPuntosDisponibles(punto);
            if (posibles.isEmpty()) {
                return rechazar(nombreJugador, "Ese punto ya no tiene líneas disponibles");
            }
            puntoSeleccionado = new PuntoSeleccionado(punto, posibles);
        }
        limpiarRechazo();
        return true;
    }


    public boolean jugarLinea(int fila, int columna, String nombreJugador) {
        if (!esTurnoDe(nombreJugador)) {
            return rechazar(nombreJugador, "No es el turno de " + nombreJugador);
        }
        if (puntoSeleccionado == null) {
            return rechazar(nombreJugador, "Primero hay que seleccionar un punto");
        }
        Punto punto1 = puntoSeleccionado.getPunto();
        Punto punto2 = tablero.getPunto(fila, columna);
        if (!tablero.validaPosicion(punto1, punto2)) {
            return rechazar(nombreJugador, "No se puede trazar una línea entre esos puntos");
        }

        Jugador jugador = jugadorActual;
        Linea linea = tablero.marcarLinea(punto1, punto2, jugador);
        List<Cuadro> cuadros = tablero.verificarCuadro(linea);
        for (Cuadro cuadro : cuadros) {
            cuadro.reclamar(jugador);
            jugador.agregarPuntaje(1);
        }
        historial.add(new Jugada(jugador, linea, cuadros));
        puntoSeleccionado = null;
        limpiarRechazo();

        if (tablero.validaLleno()) {
            finalizar();
        } else if (cuadros.isEmpty()) {
            pasaTurno();
        }
        return true;
    }

    public void pasaTurno() {
        int siguiente = (jugadores.indexOf(jugadorActual) + 1) % jugadores.size();
        jugadorActual = jugadores.get(siguiente);
    }

    public void finalizar() {
        estado = EstadoPartida.TERMINADA;
        puntoSeleccionado = null;
    }

    private boolean esTurnoDe(String nombreJugador) {
        return estado == EstadoPartida.EN_CURSO
                && jugadorActual.getNombre().equals(nombreJugador);
    }

    private boolean rechazar(String nombreJugador, String motivo) {
        this.jugadorRechazado = nombreJugador;
        this.motivoRechazo = motivo;
        return false;
    }

    private void limpiarRechazo() {
        this.jugadorRechazado = null;
        this.motivoRechazo = null;
    }

    public Jugador getJugadorActual() {
        return jugadorActual;
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public Marcador getMarcador() {
        return marcador;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public PuntoSeleccionado getPuntoSeleccionado() {
        return puntoSeleccionado;
    }

    public Jugada getUltimaJugada() {
        return historial.isEmpty() ? null : historial.get(historial.size() - 1);
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public String getJugadorRechazado() {
        return jugadorRechazado;
    }
}
