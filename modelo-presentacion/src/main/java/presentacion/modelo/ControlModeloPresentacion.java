package presentacion.modelo;

import juego.modelo.IConsultaModeloJuego;
import juego.modelo.IOperacionesModeloJuego;
import juego.objetosNegocio.Jugador;
import juego.objetosNegocio.Punto;
import juego.objetosNegocio.PuntoSeleccionado;

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
        // Pixeles -> fila/columna. Si el clic no cayó en un punto, se ignora.
        int fila = traductor.aFila(x, y);
        int columna = traductor.aColumna(x, y);
        if (fila < 0 || columna < 0
                || fila >= consultaJuego.getFilas() || columna >= consultaJuego.getColumnas()) {
            return;
        }

        // ¿Selección o línea?
        PuntoSeleccionado seleccion = consultaJuego.getPuntoSeleccionado();
        if (seleccion != null && esPosible(seleccion, fila, columna)) {
            operacionesJuego.jugarLinea(fila, columna, jugadorLocal);
        } else {
            // sin selección, el mismo punto (Partida lo deselecciona) u otro punto (cambia la selección)
            operacionesJuego.seleccionarPunto(fila, columna, jugadorLocal);
        }
    }

    public boolean esMiTurno() {
        Jugador actual = consultaJuego.getJugadorActual();
        return actual != null && actual.getNombre().equals(jugadorLocal);
    }

    private boolean esPosible(PuntoSeleccionado seleccion, int fila, int columna) {
        for (Punto p : seleccion.getPosibles()) {
            if (p.esIgual(fila, columna)) {
                return true;
            }
        }
        return false;
    }
}