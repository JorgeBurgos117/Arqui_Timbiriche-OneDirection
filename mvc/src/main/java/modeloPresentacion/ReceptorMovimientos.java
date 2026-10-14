package modeloPresentacion;

import dtos.FiguraDTO;
import dtos.JugadorDTO;
import dtos.ResultadoDTO;
import dtos.VotacionDTO;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.SwingUtilities;
import modeloJuego.EventoModelo;
import modeloJuego.IConsultaModeloJuego;
import modeloJuego.ObservadorModeloJuego;
import modeloJuego.objetosNegocio.Cuadro;
import modeloJuego.objetosNegocio.Jugada;
import modeloJuego.objetosNegocio.Jugador;
import modeloJuego.objetosNegocio.Linea;
import modeloJuego.objetosNegocio.Punto;
import modeloJuego.objetosNegocio.PuntoSeleccionado;

public class ReceptorMovimientos implements ObservadorModeloJuego {

    private final String jugadorLocal;
    private final IConsultaModeloJuego consultaJuego;
    private final TraductorModeloJuego traductor;
    private final List<ObservadorPresentacion> observadores = new CopyOnWriteArrayList<>();
    private final Queue<FiguraDTO> colaFiguras = new ConcurrentLinkedQueue<>();
    private final Queue<JugadorDTO> colaJugadores = new ConcurrentLinkedQueue<>();
    private final List<Punto> puntosResaltados = new ArrayList<>();
    private String mensajeAdvertencia;

    public ReceptorMovimientos(IConsultaModeloJuego consulta, TraductorModeloJuego traductor, String jugadorLocal) {
        this.consultaJuego = consulta;
        this.traductor = traductor;
        this.jugadorLocal = jugadorLocal;
    }

    @Override
    public void actualizar(EventoModelo evento) {
        if (SwingUtilities.isEventDispatchThread()) {
            procesar(evento);
        } else {
            SwingUtilities.invokeLater(() -> procesar(evento));
        }
    }

    private void procesar(EventoModelo evento) {
        switch (evento) {
            case SELECCION_CAMBIADA -> {
                encolarSeleccion();
                notificarObservadores(EventoPresentacion.FIGURAS_ACTUALIZADAS);
            }
            case JUGADA_REALIZADA -> {
                encolarJugada();
                encolarSeleccion();
                encolarJugadores();
                notificarObservadores(EventoPresentacion.FIGURAS_ACTUALIZADAS);
                notificarObservadores(EventoPresentacion.JUGADORES_ACTUALIZADOS);
            }
            case JUGADA_INVALIDA -> {
                if (esLocal(consultaJuego.getJugadorRechazado())) {
                    avisar(consultaJuego.getMotivoRechazo());
                }
            }
            case JUGADOR_ABANDONO -> {
                encolarTodo();
                encolarJugadores();
                notificarObservadores(EventoPresentacion.FIGURAS_ACTUALIZADAS);
                notificarObservadores(EventoPresentacion.JUGADORES_ACTUALIZADOS);
                avisar(consultaJuego.getUltimoAbandono().getNombre() + " abandonó la partida.");
            }
            case FIN_PROPUESTO -> {
                encolarSeleccion();
                notificarObservadores(EventoPresentacion.FIGURAS_ACTUALIZADAS);
                if (getVotacionPendiente() != null) {
                    notificarObservadores(EventoPresentacion.VOTACION_PENDIENTE);
                }
            }
            case FIN_RECHAZADO -> avisar("La propuesta para terminar la partida fue rechazada.");
            case PARTIDA_TERMINADA -> {
                encolarSeleccion();
                encolarJugadores();
                notificarObservadores(EventoPresentacion.FIGURAS_ACTUALIZADAS);
                notificarObservadores(EventoPresentacion.JUGADORES_ACTUALIZADOS);
                notificarObservadores(EventoPresentacion.PARTIDA_TERMINADA);
            }
        }
    }

    public void sincronizar() {
        encolarTodo();
        encolarJugadores();
        notificarObservadores(EventoPresentacion.FIGURAS_ACTUALIZADAS);
        notificarObservadores(EventoPresentacion.JUGADORES_ACTUALIZADOS);
    }

    private void avisar(String mensaje) {
        mensajeAdvertencia = mensaje;
        notificarObservadores(EventoPresentacion.ADVERTENCIA);
    }

    private void encolarTodo() {
        puntosResaltados.clear();
        for (int f = 0; f < consultaJuego.getFilas(); f++) {
            for (int c = 0; c < consultaJuego.getColumnas(); c++) {
                colaFiguras.add(traductor.aFiguraPunto(new Punto(f, c), null));
            }
        }
        for (Cuadro cuadro : consultaJuego.getCuadros()) {
            colaFiguras.add(traductor.aFiguraCuadro(cuadro));
        }
        for (Linea linea : consultaJuego.getLineasMarcadas()) {
            colaFiguras.add(traductor.aFiguraLinea(linea));
        }
        encolarSeleccion();
    }

    private void encolarSeleccion() {
        for (Punto punto : puntosResaltados) {
            colaFiguras.add(traductor.aFiguraPunto(punto, null));
        }
        puntosResaltados.clear();

        PuntoSeleccionado seleccion = consultaJuego.getPuntoSeleccionado();
        Jugador actual = consultaJuego.getJugadorActual();
        if (seleccion == null || actual == null) {
            return;
        }
        Color color = traductor.getColor(actual.getNombre());
        Punto origen = seleccion.getPunto();
        colaFiguras.add(traductor.aFiguraPunto(origen, color));
        puntosResaltados.add(origen);
        for (Punto posible : seleccion.getPosibles()) {
            colaFiguras.add(traductor.aFiguraPunto(posible, color, origen));
            puntosResaltados.add(posible);
        }
    }

    private void encolarJugada() {
        Jugada jugada = consultaJuego.getUltimaJugada();
        if (jugada == null) {
            return;
        }
        colaFiguras.add(traductor.aFiguraLinea(jugada.getLinea()));
        for (Cuadro cuadro : jugada.getCuadrosCompletados()) {
            colaFiguras.add(traductor.aFiguraCuadro(cuadro));
        }
    }

    private void encolarJugadores() {
        colaJugadores.clear();
        Jugador actual = consultaJuego.getJugadorActual();
        for (Jugador jugador : consultaJuego.getJugadores()) {
            if (jugador.isActivo()) {
                colaJugadores.add(traductor.aJugadorDTO(jugador, jugador == actual));
            }
        }
    }

    public List<FiguraDTO> sacarFiguras() {
        List<FiguraDTO> figuras = new ArrayList<>();
        FiguraDTO figura;
        while ((figura = colaFiguras.poll()) != null) {
            figuras.add(figura);
        }
        return figuras;
    }

    public List<JugadorDTO> sacarJugadores() {
        List<JugadorDTO> jugadores = new ArrayList<>();
        JugadorDTO jugador;
        while ((jugador = colaJugadores.poll()) != null) {
            jugadores.add(jugador);
        }
        return jugadores;
    }

    public String getMensajeAdvertencia() {
        return mensajeAdvertencia;
    }

    public VotacionDTO getVotacionPendiente() {
        Jugador proponente = consultaJuego.getProponenteFin();
        if (proponente == null) {
            return null;
        }
        for (Jugador pendiente : consultaJuego.getPendientesFin()) {
            if (esLocal(pendiente.getNombre())) {
                return new VotacionDTO(pendiente.getNombre(), proponente.getNombre());
            }
        }
        return null;
    }

    public List<ResultadoDTO> getResultados() {
        Jugador ganador = consultaJuego.getGanador();
        List<ResultadoDTO> resultados = new ArrayList<>();
        int posicion = 0;
        int puntajeAnterior = -1;
        int lugar = 0;
        for (Jugador jugador : consultaJuego.getPosiciones()) {
            Color color = traductor.getColor(jugador.getNombre());
            if (!jugador.isActivo()) {
                resultados.add(new ResultadoDTO(0, jugador.getNombre(), color, jugador.getPuntaje(), true, false));
                continue;
            }
            lugar++;
            if (jugador.getPuntaje() != puntajeAnterior) {
                posicion = lugar;
                puntajeAnterior = jugador.getPuntaje();
            }
            resultados.add(new ResultadoDTO(posicion, jugador.getNombre(), color, jugador.getPuntaje(),
                    false, jugador == ganador));
        }
        return resultados;
    }

    public String getTextoFin() {
        if (consultaJuego.getMotivoFin() == null) {
            return "";
        }
        String motivo = switch (consultaJuego.getMotivoFin()) {
            case TABLERO_LLENO -> "Se completaron todos los cuadros.";
            case ABANDONO -> "Los demás jugadores abandonaron la partida.";
            case ACUERDO -> "Todos acordaron terminar la partida.";
        };
        Jugador ganador = consultaJuego.getGanador();
        return motivo + (ganador == null ? " Empate." : " Ganador: " + ganador.getNombre());
    }

    public int getTamanoCelda() {
        return traductor.getTamanoCelda();
    }

    public int getLadoTablero() {
        return traductor.getLadoTablero(Math.max(consultaJuego.getFilas(), consultaJuego.getColumnas()));
    }

    private boolean esLocal(String nombre) {
        return jugadorLocal == null || jugadorLocal.equals(nombre);
    }

    public void agregarObservador(ObservadorPresentacion observador) {
        observadores.add(observador);
    }

    public void eliminarObservador(ObservadorPresentacion observador) {
        observadores.remove(observador);
    }

    private void notificarObservadores(EventoPresentacion evento) {
        for (ObservadorPresentacion observador : observadores) {
            observador.actualizar(evento);
        }
    }
}
