package mensajeria;

import java.util.concurrent.BlockingQueue;
import mensajeria.api.IManejadorMensaje;
import mensajeria.api.IMensajeAdmon;

public class ProcesadorMensajes {
    private final BlockingQueue<IMensajeAdmon> colaEntrada;
    private final IManejadorMensaje manejadorApp;
    private final IManejadorMensaje manejadorAdmon;
    private volatile boolean activo;
    private Thread hilo;
    private static final IMensajeAdmon FIN = new Mensaje();

    public ProcesadorMensajes(BlockingQueue<IMensajeAdmon> colaEntrada,
                              IManejadorMensaje manejadorApp,
                              IManejadorMensaje manejadorAdmon) {
        this.colaEntrada = colaEntrada;
        this.manejadorApp = manejadorApp;
        this.manejadorAdmon = manejadorAdmon;
    }

    public synchronized void iniciar() {
        if (activo) return;
        activo = true;
        hilo = new Thread(this::procesar, "ProcesadorMensajes");
        hilo.setDaemon(true);
        hilo.start();
    }

    public synchronized void detener() {
        if (!activo) return;
        activo = false;
        colaEntrada.offer(FIN);
        try { hilo.join(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private void procesar() {
        try {
            while (true) {
                IMensajeAdmon mensaje = colaEntrada.take();
                if (mensaje == FIN) return;
                String contenido = mensaje.getContenido();
                if (contenido != null && contenido.startsWith("#ADM:")) {
                    manejadorAdmon.manejar(mensaje);
                } else {
                    manejadorApp.manejar(mensaje);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
