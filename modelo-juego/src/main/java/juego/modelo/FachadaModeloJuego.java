package juego.modelo;

import juego.objetosNegocio.*;

import java.util.List;

public class FachadaModeloJuego extends SujetoObservado
        implements IOperacionesModeloJuego, IConsultaModeloJuego {

    private final Partida partida;

    public FachadaModeloJuego(Partida partida) {
        this.partida = partida;
    }

    @Override
    public synchronized void seleccionarPunto(int fila, int columna, String nombreJugador) {
        boolean aceptada = partida.seleccionarPunto(fila, columna, nombreJugador);
        notificarObservadores(aceptada ? EventoModelo.SELECCION_CAMBIADA : EventoModelo.JUGADA_INVALIDA);
    }

    @Override
    public synchronized void jugarLinea(int fila, int columna, String nombreJugador) {
        boolean aceptada = partida.jugarLinea(fila, columna, nombreJugador);
        if (!aceptada) {
            notificarObservadores(EventoModelo.JUGADA_INVALIDA);
            return;
        }
        notificarObservadores(EventoModelo.JUGADA_REALIZADA);
        if (partida.getEstado() == EstadoPartida.TERMINADA) {
            notificarObservadores(EventoModelo.PARTIDA_TERMINADA);
        }
    }

    @Override
    public synchronized int getFilas() {
        return partida.getTablero().getFilas();
    }

    @Override
    public synchronized int getColumnas() {
        return partida.getTablero().getColumnas();
    }

    @Override
    public synchronized List<Linea> getLineasMarcadas() {
        return List.copyOf(partida.getTablero().getLineasMarcadas());
    }

    @Override
    public synchronized List<Cuadro> getCuadros() {
        return List.copyOf(partida.getTablero().getCuadros());
    }

    @Override
    public synchronized PuntoSeleccionado getPuntoSeleccionado() {
        return partida.getPuntoSeleccionado();
    }

    @Override
    public synchronized Jugada getUltimaJugada() {
        return partida.getUltimaJugada();
    }

    @Override
    public synchronized List<Jugador> getJugadores() {
        return List.copyOf(partida.getJugadores());
    }

    @Override
    public synchronized Jugador getJugadorActual() {
        return partida.getJugadorActual();
    }

    @Override
    public synchronized EstadoPartida getEstadoPartida() {
        return partida.getEstado();
    }

    @Override
    public synchronized String getMotivoRechazo() {
        return partida.getMotivoRechazo();
    }

    @Override
    public synchronized String getJugadorRechazado() {
        return partida.getJugadorRechazado();
    }
}