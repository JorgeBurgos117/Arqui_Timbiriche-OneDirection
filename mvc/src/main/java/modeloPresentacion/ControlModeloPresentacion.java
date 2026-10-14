package modeloPresentacion;

import java.awt.Color;
import java.util.HashSet;
import java.util.Map;
import modeloJuego.IConsultaModeloJuego;
import modeloJuego.IOperacionesModeloJuego;
import modeloJuego.objetosNegocio.EstadoPartida;
import modeloJuego.objetosNegocio.Jugador;
import modeloJuego.objetosNegocio.PuntoSeleccionado;

public class ControlModeloPresentacion {

    private final String jugadorLocal;
    private final IOperacionesModeloJuego operacionesJuego;
    private final IConsultaModeloJuego consultaJuego;
    private final TraductorModeloJuego traductor;

    public ControlModeloPresentacion(IOperacionesModeloJuego operaciones, IConsultaModeloJuego consulta,
                                     TraductorModeloJuego traductor, String jugadorLocal) {
        this.operacionesJuego = operaciones;
        this.consultaJuego = consulta;
        this.traductor = traductor;
        this.jugadorLocal = jugadorLocal;
    }

    public void seleccionarPunto(int x, int y) {
        int fila = traductor.aFila(y);
        int columna = traductor.aColumna(x);
        String nombre = jugadorQueActua();
        if (fila < 0 || columna < 0 || nombre == null) {
            return;
        }
        PuntoSeleccionado seleccion = consultaJuego.getPuntoSeleccionado();
        if (seleccion == null || seleccion.getPunto().esIgual(fila, columna)) {
            operacionesJuego.seleccionarPunto(fila, columna, nombre);
        } else {
            operacionesJuego.jugarLinea(fila, columna, nombre);
        }
    }

    public void cancelarSeleccion() {
        String nombre = jugadorQueActua();
        if (nombre != null) {
            operacionesJuego.cancelarSeleccion(nombre);
        }
    }

    public boolean esMiTurno() {
        Jugador actual = consultaJuego.getJugadorActual();
        return consultaJuego.getEstadoPartida() == EstadoPartida.EN_CURSO
                && consultaJuego.getProponenteFin() == null
                && actual != null
                && (jugadorLocal == null || jugadorLocal.equals(actual.getNombre()));
    }

    public boolean cambiarColores(Map<String, Color> colores) {
        if (colores.containsValue(null) || new HashSet<>(colores.values()).size() != colores.size()) {
            return false;
        }
        colores.forEach(traductor::asignarColor);
        return true;
    }

    public void abandonar() {
        String nombre = jugadorQueActua();
        if (nombre != null) {
            operacionesJuego.abandonar(nombre);
        }
    }

    public void proponerFin() {
        String nombre = jugadorQueActua();
        if (nombre != null) {
            operacionesJuego.proponerFin(nombre);
        }
    }

    public void votarFin(String votante, boolean acepta) {
        if (jugadorLocal == null || jugadorLocal.equals(votante)) {
            operacionesJuego.votarFin(votante, acepta);
        }
    }

    private String jugadorQueActua() {
        if (jugadorLocal != null) {
            return jugadorLocal;
        }
        Jugador actual = consultaJuego.getJugadorActual();
        return actual == null ? null : actual.getNombre();
    }
}
