package mensajeria;

import java.util.concurrent.atomic.AtomicReference;
import mensajeria.api.IDirectorio;
import mensajeria.api.IManejadorMensaje;
import mensajeria.api.IMensajeAdmon;
import mensajeria.api.IReceptorMensajes;
import mensajeria.api.Nodo;

public class ManejadorAdministracion implements IManejadorMensaje {
    private final IDirectorio directorio;
    private final AtomicReference<IReceptorMensajes> receptor =
            new AtomicReference<>();

    public ManejadorAdministracion(IDirectorio directorio) {
        this.directorio = directorio;
    }

    public void setReceptor(IReceptorMensajes receptor) {
        this.receptor.set(receptor);
    }

    @Override
    public void manejar(IMensajeAdmon mensaje) {
        String contenido = mensaje.getContenido();
        if ("#ADM:CONFIRMAR_REGISTRO".equals(contenido)) {
            IReceptorMensajes destino = receptor.get();
            if (destino != null) destino.estadoBuzon(true);
        } else if (contenido != null && contenido.startsWith("#ADM:DIRECTORIO:")) {
            String[] partes = contenido.substring("#ADM:DIRECTORIO:".length()).split(":", 3);
            if (partes.length == 3) {
                try {
                    directorio.registrar(partes[0], new Nodo(partes[1], Integer.parseInt(partes[2])));
                } catch (NumberFormatException ignored) {
                }
            }
        } else if (contenido != null && contenido.startsWith("#ADM:BAJA:")) {
            directorio.eliminar(contenido.substring("#ADM:BAJA:".length()));
        }
    }
}
