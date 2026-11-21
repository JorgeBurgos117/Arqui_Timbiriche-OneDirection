package mensajeria;

import java.util.concurrent.BlockingQueue;

public class DispatcherMensajes {

    private final IDirectorio directorio;
    private final BlockingQueue<IMensajeAdmon> colaSalida;

    public DispatcherMensajes(IDirectorio directorio, BlockingQueue<IMensajeAdmon> colaSalida) {
        this.directorio = directorio;
        this.colaSalida = colaSalida;
    }

    public void enviar(IMensajeAdmon mensaje) throws DestinoNoEncontradoException {
        Nodo nodo = directorio.buscar(mensaje.getDestinatario());
        mensaje.setIpDestino(nodo.ip());
        mensaje.setPuertoDestino(nodo.puerto());
        colaSalida.add(mensaje);
    }
}
