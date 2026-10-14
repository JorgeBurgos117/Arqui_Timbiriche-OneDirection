package modeloJuego.objetosNegocio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Partida {

    private final Tablero tablero;
    private final Marcador marcador;
    private final List<Jugador> jugadores;
    private Jugador jugadorActual;
    private EstadoPartida estado = EstadoPartida.EN_CURSO;
    private final List<Jugada> historial = new ArrayList<>();
    private PuntoSeleccionado puntoSeleccionado;
    private String motivoRechazo;
    private String jugadorRechazado;

    private MotivoFin motivoFin;
    private Jugador ultimoAbandono;
    private Jugador proponenteFin;
    private final List<Jugador> pendientesFin = new ArrayList<>();

    public Partida(Tablero tablero, List<Jugador> jugadores) {
        if (jugadores.size() < 2 || jugadores.size() > 4) {
            throw new IllegalArgumentException("Solo se admiten de 2 a 4 jugadores, llegaron " + jugadores.size());
        }
        this.tablero = tablero;
        this.jugadores = List.copyOf(jugadores);
        this.marcador = new Marcador(this.jugadores);
        this.jugadorActual = this.jugadores.get(0);
    }

    public boolean seleccionarPunto(int fila, int columna, String nombreJugador) {
        if (!esTurnoDe(nombreJugador)) {
            return rechazar(nombreJugador, motivoFueraDeTurno());
        }
        Punto punto = tablero.getPunto(fila, columna);
        if (punto == null) {
            return rechazar(nombreJugador, "Ese punto no está en el tablero.");
        }
        if (puntoSeleccionado != null && puntoSeleccionado.getPunto().equals(punto)) {
            puntoSeleccionado = null;
            return true;
        }
        List<Punto> posibles = tablero.buscarPuntosDisponibles(punto);
        if (posibles.isEmpty()) {
            return rechazar(nombreJugador, "Ese punto ya no tiene líneas disponibles.");
        }
        puntoSeleccionado = new PuntoSeleccionado(punto, posibles);
        return true;
    }

    public boolean cancelarSeleccion(String nombreJugador) {
        if (!esTurnoDe(nombreJugador) || puntoSeleccionado == null) {
            return false;
        }
        puntoSeleccionado = null;
        return true;
    }

    public boolean jugarLinea(int fila, int columna, String nombreJugador) {
        if (!esTurnoDe(nombreJugador)) {
            return rechazar(nombreJugador, motivoFueraDeTurno());
        }
        if (puntoSeleccionado == null) {
            return rechazar(nombreJugador, "Primero selecciona el punto de origen.");
        }
        Punto punto1 = puntoSeleccionado.getPunto();
        Punto punto2 = tablero.getPunto(fila, columna);
        if (!tablero.validaPosicion(punto1, punto2)) {
            return rechazar(nombreJugador, "Solo puedes unir el punto seleccionado con un vecino libre.");
        }

        Jugador jugador = jugadorActual;
        Linea linea = tablero.marcarLinea(punto1, punto2, jugador);
        List<Cuadro> cuadrosCompletados = tablero.verificarCuadro(linea);
        for (Cuadro cuadro : cuadrosCompletados) {
            cuadro.reclamar(jugador);
            jugador.agregarPuntaje(1);
        }
        historial.add(new Jugada(jugador, linea, cuadrosCompletados));
        puntoSeleccionado = null;

        if (tablero.validaLleno()) {
            motivoFin = MotivoFin.TABLERO_LLENO;
            finalizar();
        } else if (cuadrosCompletados.isEmpty()) {
            pasaTurno();
        }
        return true;
    }

    public boolean abandonar(String nombreJugador) {
        Jugador jugador = buscarJugador(nombreJugador);
        if (estado != EstadoPartida.EN_CURSO || jugador == null || !jugador.isActivo()) {
            return rechazar(nombreJugador, "No puedes abandonar en este momento.");
        }
        boolean estabaEnTurno = jugador == jugadorActual;
        jugador.abandonar();
        ultimoAbandono = jugador;

        if (proponenteFin == jugador) {
            descartarPropuesta();
        } else {
            pendientesFin.remove(jugador);
        }

        List<Jugador> activos = getActivos();
        if (activos.size() == 1) {
            motivoFin = MotivoFin.ABANDONO;
            finalizar();
            return true;
        }
        if (estabaEnTurno) {
            puntoSeleccionado = null;
            pasaTurno();
        }
        if (proponenteFin != null && pendientesFin.isEmpty()) {
            motivoFin = MotivoFin.ACUERDO;
            finalizar();
        }
        return true;
    }

    public boolean proponerFin(String nombreJugador) {
        Jugador jugador = buscarJugador(nombreJugador);
        if (estado != EstadoPartida.EN_CURSO || jugador == null || !jugador.isActivo() || proponenteFin != null) {
            return rechazar(nombreJugador, "No se puede proponer terminar la partida en este momento.");
        }
        proponenteFin = jugador;
        pendientesFin.clear();
        for (Jugador j : getActivos()) {
            if (j != jugador) {
                pendientesFin.add(j);
            }
        }
        puntoSeleccionado = null;
        return true;
    }

    public boolean votarFin(String nombreJugador, boolean acepta) {
        Jugador jugador = buscarJugador(nombreJugador);
        if (estado != EstadoPartida.EN_CURSO || jugador == null || !pendientesFin.contains(jugador)) {
            return rechazar(nombreJugador, "No hay una votación pendiente para ti.");
        }
        if (!acepta) {
            descartarPropuesta();
            return true;
        }
        pendientesFin.remove(jugador);
        if (pendientesFin.isEmpty()) {
            motivoFin = MotivoFin.ACUERDO;
            finalizar();
        }
        return true;
    }

    public void pasaTurno() {
        int actual = jugadores.indexOf(jugadorActual);
        for (int paso = 1; paso <= jugadores.size(); paso++) {
            Jugador siguiente = jugadores.get((actual + paso) % jugadores.size());
            if (siguiente.isActivo()) {
                jugadorActual = siguiente;
                return;
            }
        }
    }

    public void finalizar() {
        estado = EstadoPartida.TERMINADA;
        puntoSeleccionado = null;
        descartarPropuesta();
    }

    private void descartarPropuesta() {
        proponenteFin = null;
        pendientesFin.clear();
    }

    private boolean esTurnoDe(String nombreJugador) {
        return estado == EstadoPartida.EN_CURSO
                && proponenteFin == null
                && jugadorActual.getNombre().equals(nombreJugador);
    }

    private String motivoFueraDeTurno() {
        if (estado != EstadoPartida.EN_CURSO) {
            return "La partida ya terminó.";
        }
        if (proponenteFin != null) {
            return "Hay una votación pendiente para terminar la partida.";
        }
        return "No es tu turno.";
    }

    private boolean rechazar(String nombreJugador, String motivo) {
        this.motivoRechazo = motivo;
        this.jugadorRechazado = nombreJugador;
        return false;
    }

    private Jugador buscarJugador(String nombre) {
        for (Jugador j : jugadores) {
            if (j.getNombre().equals(nombre)) {
                return j;
            }
        }
        return null;
    }

    private List<Jugador> getActivos() {
        List<Jugador> activos = new ArrayList<>();
        for (Jugador j : jugadores) {
            if (j.isActivo()) {
                activos.add(j);
            }
        }
        return activos;
    }

    public Jugador getJugadorActual() {
        return estado == EstadoPartida.EN_CURSO ? jugadorActual : null;
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public Tablero getTablero() {
        return tablero;
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

    public MotivoFin getMotivoFin() {
        return motivoFin;
    }

    public Jugador getGanador() {
        return estado == EstadoPartida.TERMINADA ? marcador.getGanador() : null;
    }

    public List<Jugador> getPosiciones() {
        return marcador.calcularPosiciones();
    }

    public Jugador getUltimoAbandono() {
        return ultimoAbandono;
    }

    public Jugador getProponenteFin() {
        return proponenteFin;
    }

    public List<Jugador> getPendientesFin() {
        return Collections.unmodifiableList(pendientesFin);
    }
}
