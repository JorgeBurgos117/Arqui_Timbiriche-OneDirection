package mensajeria;

import java.util.concurrent.BlockingQueue;
import red.api.IReceptorExterno;

public class ReceptorMensajes implements IReceptorExterno {

    private final ISerializador serializador;
    private final BlockingQueue<IMensajeAdmon> colaEntrada;

    public ReceptorMensajes(ISerializador serializador, BlockingQueue<IMensajeAdmon> colaEntrada) {
        this.serializador = serializador;
        this.colaEntrada = colaEntrada;
    }

    @Override
    public void recibir(String texto) {
        try {
            colaEntrada.add(serializador.deserializar(texto));
        } catch (SerializacionException e) {
            System.err.println("[mensajeria] Mensaje descartado al deserializar: " + e.getMessage());
        }
    }
}
