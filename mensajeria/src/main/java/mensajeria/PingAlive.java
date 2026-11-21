package mensajeria;

import java.util.concurrent.BlockingQueue;

public class PingAlive implements Runnable {

    private final IFabricaMensajes fabrica;
    private final IDirectorio directorio;
    private final BlockingQueue<IMensajeAdmon> colaSalida;
    private final long intervaloMs;
    private Thread hilo;

    public PingAlive(IFabricaMensajes fabrica, IDirectorio directorio,
                     BlockingQueue<IMensajeAdmon> colaSalida, long intervaloMs) {
        this.fabrica = fabrica;
        this.directorio = directorio;
        this.colaSalida = colaSalida;
        this.intervaloMs = intervaloMs;
    }

    public synchronized void iniciar() {
        if (hilo != null && hilo.isAlive()) {
            return;
        }
        hilo = new Thread(this, "mensajeria-ping");
        hilo.setDaemon(true);
        hilo.start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Nodo servidor = directorio.buscar(IMensajeAdmon.SERVIDOR);
                IMensajeAdmon ping = fabrica.crearMensaje();
                ping.setDestinatario(IMensajeAdmon.SERVIDOR);
                ping.setContenido(IMensajeAdmon.PING);
                ping.setIpDestino(servidor.ip());
                ping.setPuertoDestino(servidor.puerto());
                colaSalida.add(ping);
                Thread.sleep(intervaloMs);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public synchronized void detener() {
        if (hilo != null) {
            hilo.interrupt();
            hilo = null;
        }
    }
}
