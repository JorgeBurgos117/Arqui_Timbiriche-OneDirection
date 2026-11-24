package mensajeria;

import java.util.concurrent.BlockingQueue;
import mensajeria.api.IMensajeAdmon;
import mensajeria.api.ISerializador;
import red.api.IDispatcher;

public class Sender {
    private final BlockingQueue<IMensajeAdmon> colaSalida;
    private final IDispatcher red;
    private final ISerializador serializador = new SerializadorJSON();
    private volatile boolean activo;
    private Thread hilo;
    private static final IMensajeAdmon FIN = new Mensaje();

    public Sender(BlockingQueue<IMensajeAdmon> colaSalida, IDispatcher red) {
        this.colaSalida = colaSalida;
        this.red = red;
    }

    public synchronized void iniciar() {
        if (activo) return;
        activo = true;
        hilo = new Thread(this::procesar, "SenderMensajes");
        hilo.setDaemon(true);
        hilo.start();
    }

    public synchronized void detener() {
        if (!activo) return;
        activo = false;
        colaSalida.offer(FIN);
        try { hilo.join(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private void procesar() {
        try {
            while (true) {
                IMensajeAdmon mensaje = colaSalida.take();
                if (mensaje == FIN) return;
                red.enviar(serializador.serializar(mensaje), mensaje.getIpDestino(), mensaje.getPuertoDestino());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            throw new RuntimeException("Error al serializar o enviar", e);
        }
    }
}
