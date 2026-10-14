package modeloJuego;

import java.util.List;
import modeloJuego.objetosNegocio.Cuadro;
import modeloJuego.objetosNegocio.EstadoPartida;
import modeloJuego.objetosNegocio.Jugada;
import modeloJuego.objetosNegocio.Jugador;
import modeloJuego.objetosNegocio.Linea;
import modeloJuego.objetosNegocio.MotivoFin;
import modeloJuego.objetosNegocio.Partida;
import modeloJuego.objetosNegocio.PuntoSeleccionado;

public class FachadaModeloJuego extends SujetoObservado implements IOperacionesModeloJuego, IConsultaModeloJuego {

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
        if (!partida.jugarLinea(fila, columna, nombreJugador)) {
            notificarObservadores(EventoModelo.JUGADA_INVALIDA);
            return;
        }
        notificarObservadores(EventoModelo.JUGADA_REALIZADA);
        notificarSiTermino();
    }

    @Override
    public synchronized void cancelarSeleccion(String nombreJugador) {
        if (partida.cancelarSeleccion(nombreJugador)) {
            notificarObservadores(EventoModelo.SELECCION_CAMBIADA);
        }
    }

    @Override
    public synchronized void abandonar(String nombreJugador) {
        if (!partida.abandonar(nombreJugador)) {
            notificarObservadores(EventoModelo.JUGADA_INVALIDA);
            return;
        }
        notificarObservadores(EventoModelo.JUGADOR_ABANDONO);
        notificarSiTermino();
    }

    @Override
    public synchronized void proponerFin(String nombreJugador) {
        boolean aceptada = partida.proponerFin(nombreJugador);
        notificarObservadores(aceptada ? EventoModelo.FIN_PROPUESTO : EventoModelo.JUGADA_INVALIDA);
    }

    @Override
    public synchronized void votarFin(String nombreJugador, boolean acepta) {
        if (!partida.votarFin(nombreJugador, acepta)) {
            notificarObservadores(EventoModelo.JUGADA_INVALIDA);
        } else if (!acepta) {
            notificarObservadores(EventoModelo.FIN_RECHAZADO);
        } else if (partida.getEstado() == EstadoPartida.TERMINADA) {
            notificarObservadores(EventoModelo.PARTIDA_TERMINADA);
        } else {
            notificarObservadores(EventoModelo.FIN_PROPUESTO);
        }
    }

    private void notificarSiTermino() {
        if (partida.getEstado() == EstadoPartida.TERMINADA) {
            notificarObservadores(EventoModelo.PARTIDA_TERMINADA);
        }
    }

    @Override
    public int getFilas() {
        return partida.getTablero().getFilas();
    }

    @Override
    public int getColumnas() {
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
    public List<Jugador> getJugadores() {
        return partida.getJugadores();
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

    @Override
    public synchronized MotivoFin getMotivoFin() {
        return partida.getMotivoFin();
    }

    @Override
    public synchronized Jugador getGanador() {
        return partida.getGanador();
    }

    @Override
    public synchronized List<Jugador> getPosiciones() {
        return partida.getPosiciones();
    }

    @Override
    public synchronized Jugador getUltimoAbandono() {
        return partida.getUltimoAbandono();
    }

    @Override
    public synchronized Jugador getProponenteFin() {
        return partida.getProponenteFin();
    }

    @Override
    public synchronized List<Jugador> getPendientesFin() {
        return List.copyOf(partida.getPendientesFin());
    }
}
