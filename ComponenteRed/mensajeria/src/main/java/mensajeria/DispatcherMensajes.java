package mensajeria;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import mensajeria.api.IDirectorio;
import mensajeria.api.IMensajeAdmon;
import mensajeria.api.Nodo;
import red.api.IDispatcher;

public class DispatcherMensajes {
    private final IDirectorio directorio;
    private final BlockingQueue<IMensajeAdmon> colaSalida = new LinkedBlockingQueue<>();
    private final IDispatcher red;
    private final Sender sender;

    public DispatcherMensajes(IDirectorio directorio, IDispatcher red) {
        this.directorio = directorio;
        this.red = red;
        this.sender = new Sender(colaSalida, red);
        this.sender.iniciar();
    }

    public void enviar(IMensajeAdmon mensaje) {
        Nodo destino = directorio.buscar(mensaje.getDestinatario());
        if (destino == null) {
            throw new IllegalArgumentException("Destino no encontrado: " + mensaje.getDestinatario());
        }
        mensaje.setIpDestino(destino.ip());
        mensaje.setPuertoDestino(destino.puerto());
        colaSalida.offer(mensaje);
    }

    public void detener() {
        sender.detener();
    }
}
