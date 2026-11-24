package mensajeria;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import mensajeria.api.IMensajeAdmon;
import red.api.IReceptorExterno;

public class ReceptorMensajes implements IReceptorExterno {
    private final BlockingQueue<IMensajeAdmon> colaEntrada = new LinkedBlockingQueue<>();
    private final SerializadorJSON serializador = new SerializadorJSON();
    private volatile boolean activo = true;

    public BlockingQueue<IMensajeAdmon> getColaEntrada() {
        return colaEntrada;
    }

    @Override
    public void recibir(String texto) {
        if (!activo) return;
        try {
            colaEntrada.offer(serializador.deserializar(texto));
        } catch (RuntimeException e) {
            // Un texto no valido no detiene el receptor.
        }
    }

    public void detener() {
        activo = false;
    }
}
