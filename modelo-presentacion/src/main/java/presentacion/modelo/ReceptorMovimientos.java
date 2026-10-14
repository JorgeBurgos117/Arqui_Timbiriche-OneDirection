package presentacion.modelo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.SwingUtilities;
import juego.modelo.EventoModelo;
import juego.modelo.IConsultaModeloJuego;
import juego.modelo.IObservadorModeloJuego;
import juego.objetosNegocio.Cuadro;
import juego.objetosNegocio.Jugada;
import juego.objetosNegocio.Jugador;
import juego.objetosNegocio.Punto;
import juego.objetosNegocio.PuntoSeleccionado;
import presentacion.dtos.FiguraDTO;
import presentacion.dtos.JugadorDTO;

public class ReceptorMovimientos implements IObservadorModeloJuego {

    private static final Color COLOR_NEUTRO = Color.GRAY;

    private final IConsultaModeloJuego consultaJuego;
    private final TraductorModeloJuego traductor;
    private final String jugadorLocal;

    private final List<IObservadorPresentacion> observadores = new CopyOnWriteArrayList<>();
    private final Queue<FiguraDTO> colaFiguras = new LinkedList<>();
    private final Queue<JugadorDTO> colaJugadores = new LinkedList<>();
    private String mensajeAdvertencia;

    // puntos que están pintados de color (seleccionado + posibles) para regresarlos a gris después
    private final List<Punto> puntosResaltados = new ArrayList<>();

    public ReceptorMovimientos(IConsultaModeloJuego consulta, TraductorModeloJuego traductor, String jugadorLocal) {
        this.consultaJuego = consulta;
        this.traductor = traductor;
        this.jugadorLocal = jugadorLocal;
    }

    // Lo llama el Ensamblador al inicio: encola todos los puntos en gris y los jugadores.
    public synchronized void cargarEstadoInicial() {
        for (int f = 0; f < consultaJuego.getFilas(); f++) {
            for (int c = 0; c < consultaJuego.getColumnas(); c++) {
                colaFiguras.add(traductor.aFiguraPunto(new Punto(f, c), COLOR_NEUTRO));
            }
        }
        encolarJugadores();
        notificarObservadores(EventoPresentacion.FIGURAS_ACTUALIZADAS);
        notificarObservadores(EventoPresentacion.JUGADORES_ACTUALIZADOS);
    }

    // ===================== lo llama el Modelo de Juego =====================

    @Override
    public synchronized void actualizar(EventoModelo evento) {
        switch (evento) {
            case SELECCION_CAMBIADA -> {
                encolarSeleccion();
                notificarObservadores(EventoPresentacion.FIGURAS_ACTUALIZADAS);
            }
            case JUGADA_REALIZADA -> {
                encolarJugada();
                encolarJugadores();
                notificarObservadores(EventoPresentacion.FIGURAS_ACTUALIZADAS);
                notificarObservadores(EventoPresentacion.JUGADORES_ACTUALIZADOS);
            }
            case JUGADA_INVALIDA -> {
                // solo le avisa a SU jugador si el rechazado fue él
                if (jugadorLocal.equals(consultaJuego.getJugadorRechazado())) {
                    mensajeAdvertencia = consultaJuego.getMotivoRechazo();
                    notificarObservadores(EventoPresentacion.ADVERTENCIA);
                }
            }
            case PARTIDA_TERMINADA -> {
                encolarJugadores();
                notificarObservadores(EventoPresentacion.JUGADORES_ACTUALIZADOS);
                notificarObservadores(EventoPresentacion.PARTIDA_TERMINADA);
            }
        }
    }

    // ===================== encolar =====================

    private void encolarSeleccion() {
        apagarResaltados();
        PuntoSeleccionado sel = consultaJuego.getPuntoSeleccionado();
        if (sel == null) {
            return; // se deseleccionó: con apagar basta
        }
        Color color = traductor.colorDe(consultaJuego.getJugadorActual());
        colaFiguras.add(traductor.aFiguraPunto(sel.getPunto(), color));
        puntosResaltados.add(sel.getPunto());
        for (Punto posible : sel.getPosibles()) {          // de 1 a 4 puntos disponibles
            colaFiguras.add(traductor.aFiguraPunto(posible, color.brighter()));
            puntosResaltados.add(posible);
        }
    }

    private void encolarJugada() {
        apagarResaltados();
        Jugada jugada = consultaJuego.getUltimaJugada();
        colaFiguras.add(traductor.aFiguraLinea(jugada.getLinea()));
        for (Cuadro cuadro : jugada.getCuadrosCompletados()) {
            colaFiguras.add(traductor.aFiguraCuadro(cuadro));
        }
    }

    private void encolarJugadores() {
        Jugador actual = consultaJuego.getJugadorActual();
        for (Jugador j : consultaJuego.getJugadores()) {
            colaJugadores.add(traductor.aJugadorDTO(j, j.equals(actual)));
        }
    }

    // Regresa a gris los puntos que estaban resaltados (misma clave = la vista los reemplaza). 
    private void apagarResaltados() {
        for (Punto p : puntosResaltados) {
            colaFiguras.add(traductor.aFiguraPunto(p, COLOR_NEUTRO));
        }
        puntosResaltados.clear();
    }

    // ===================== lo piden las vistas (por la fachada) =====================

    public synchronized List<FiguraDTO> sacarFiguras() {
        List<FiguraDTO> lista = new ArrayList<>();
        while (!colaFiguras.isEmpty()) {
            lista.add(colaFiguras.poll());
        }
        return lista;
    }

    public synchronized List<JugadorDTO> sacarJugadores() {
        List<JugadorDTO> lista = new ArrayList<>();
        while (!colaJugadores.isEmpty()) {
            lista.add(colaJugadores.poll());
        }
        return lista;
    }

    public synchronized String getMensajeAdvertencia() {
        return mensajeAdvertencia;
    }

    public void agregarObservador(IObservadorPresentacion observador) {
        observadores.add(observador);
    }

    public void eliminarObservador(IObservadorPresentacion observador) {
        observadores.remove(observador);
    }

    // Siempre avisa en el hilo de Swing (las vistas solo se pueden tocar desde ahí).
    private void notificarObservadores(EventoPresentacion evento) {
        for (IObservadorPresentacion o : observadores) {
            SwingUtilities.invokeLater(() -> o.actualizar(evento));
        }
    }
}