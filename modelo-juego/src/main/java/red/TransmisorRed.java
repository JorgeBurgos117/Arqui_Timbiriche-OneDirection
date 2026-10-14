package red;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import modeloJuego.EventoModelo;
import modeloJuego.IConsultaModeloJuego;
import modeloJuego.ObservadorModeloJuego;
import modeloJuego.objetosNegocio.Jugada;
import modeloJuego.objetosNegocio.Jugador;
import modeloJuego.objetosNegocio.PuntoSeleccionado;

public class TransmisorRed implements ObservadorModeloJuego {

    private final IConsultaModeloJuego consulta;
    private final BlockingQueue<String> colaSalida = new LinkedBlockingQueue<>();
    private Thread hilo;

    public TransmisorRed(IConsultaModeloJuego consulta) {
        this.consulta = consulta;
    }

    @Override
    public void actualizar(EventoModelo evento) {
        String mensaje = describir(evento);
        if (mensaje != null) {
            colaSalida.add(mensaje);
        }
    }

    private String describir(EventoModelo evento) {
        return switch (evento) {
            case SELECCION_CAMBIADA -> {
                PuntoSeleccionado sel = consulta.getPuntoSeleccionado();
                Jugador actual = consulta.getJugadorActual();
                String quien = actual == null ? "?" : actual.getNombre();
                yield sel == null ? quien + " quita su selección" : quien + " selecciona " + sel.getPunto();
            }
            case JUGADA_REALIZADA -> {
                Jugada jugada = consulta.getUltimaJugada();
                String cierre = jugada.cerroCuadro() ? " y cierra " + jugada.getCuadrosCompletados().size() + " cuadro(s)" : "";
                yield jugada.getJugador().getNombre() + " traza " + jugada.getLinea() + cierre;
            }
            case JUGADOR_ABANDONO -> consulta.getUltimoAbandono().getNombre() + " abandona la partida";
            case FIN_PROPUESTO -> consulta.getProponenteFin() == null
                    ? null
                    : consulta.getProponenteFin().getNombre() + " propone terminar; faltan " + consulta.getPendientesFin();
            case FIN_RECHAZADO -> "la propuesta para terminar fue rechazada";
            case PARTIDA_TERMINADA -> "la partida terminó (" + consulta.getMotivoFin() + ")";
            case JUGADA_INVALIDA -> null;
        };
    }

    public synchronized void iniciar() {
        if (hilo != null) {
            return;
        }
        hilo = new Thread(this::enviar, "transmisor-red");
        hilo.setDaemon(true);
        hilo.start();
    }

    public synchronized void detener() {
        if (hilo != null) {
            hilo.interrupt();
            hilo = null;
        }
    }

    private void enviar() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("[RED] -> " + colaSalida.take());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
