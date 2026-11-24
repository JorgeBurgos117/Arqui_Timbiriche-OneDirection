package mensajeria;

import java.util.concurrent.atomic.AtomicReference;
import mensajeria.api.IManejadorMensaje;
import mensajeria.api.IMensajeAdmon;
import mensajeria.api.IMensajeApp;
import mensajeria.api.IReceptorMensajes;

public class ManejadorAplicacion implements IManejadorMensaje {
    private final AtomicReference<IReceptorMensajes> receptor =
            new AtomicReference<>();

    public void setReceptor(IReceptorMensajes receptor) {
        this.receptor.set(receptor);
    }

    @Override
    public void manejar(IMensajeAdmon mensaje) {
        IReceptorMensajes destino = receptor.get();
        if (destino != null) destino.recibir((IMensajeApp) mensaje);
    }
}
