package mensajeria;

import java.util.concurrent.BlockingQueue;

public class ProcesadorMensajes extends Thread {

    private final BlockingQueue<IMensajeAdmon> colaEntrada;
    private final IManejadorMensaje manejadorApp;
    private final IManejadorMensaje manejadorAdmon;

    public ProcesadorMensajes(BlockingQueue<IMensajeAdmon> colaEntrada,
                              IManejadorMensaje manejadorApp, IManejadorMensaje manejadorAdmon) {
        super("mensajeria-procesador");
        setDaemon(true);
        this.colaEntrada = colaEntrada;
        this.manejadorApp = manejadorApp;
        this.manejadorAdmon = manejadorAdmon;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            IMensajeAdmon m;
            try {
                m = colaEntrada.take();
            } catch (InterruptedException e) {
                return;
            }
            try {
                (IMensajeAdmon.esAdministrativo(m) ? manejadorAdmon : manejadorApp).manejar(m);
            } catch (RuntimeException e) {
                System.err.println("[mensajeria] Error manejando " + m + ": " + e.getMessage());
            }
        }
    }

    public void detener() {
        interrupt();
    }
}
