package mensajeria;

import java.util.concurrent.BlockingQueue;
import red.api.IDispatcher;

public class Sender extends Thread {

    private final BlockingQueue<IMensajeAdmon> colaSalida;
    private final ISerializador serializador;
    private final IDispatcher red;

    public Sender(BlockingQueue<IMensajeAdmon> colaSalida, ISerializador serializador, IDispatcher red) {
        super("mensajeria-sender");
        setDaemon(true);
        this.colaSalida = colaSalida;
        this.serializador = serializador;
        this.red = red;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                IMensajeAdmon m = colaSalida.take();
                red.enviar(serializador.serializar(m), m.getIpDestino(), m.getPuertoDestino());
            } catch (InterruptedException e) {
                return;
            } catch (SerializacionException e) {
                System.err.println("[mensajeria] Mensaje descartado al serializar: " + e.getMessage());
            }
        }
    }

    public void detener() {
        interrupt();
    }
}
