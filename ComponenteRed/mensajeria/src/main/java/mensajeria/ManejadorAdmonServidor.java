package mensajeria;

import mensajeria.api.IDirectorio;
import mensajeria.api.IFabricaMensajes;
import mensajeria.api.IManejadorMensaje;
import mensajeria.api.IMensajeAdmon;
import mensajeria.api.Nodo;

public class ManejadorAdmonServidor implements IManejadorMensaje {
    private final IDirectorio directorio;
    private final IFabricaMensajes fabrica;
    private final DispatcherMensajes dispatcher;

    public ManejadorAdmonServidor(IDirectorio directorio, IFabricaMensajes fabrica,
                                  DispatcherMensajes dispatcher) {
        this.directorio = directorio;
        this.fabrica = fabrica;
        this.dispatcher = dispatcher;
    }

    @Override
    public void manejar(IMensajeAdmon mensaje) {
        String contenido = mensaje.getContenido();
        if (contenido != null && contenido.startsWith("#ADM:REGISTRAR:")) {
            String nombre = contenido.substring("#ADM:REGISTRAR:".length());
            directorio.registrar(nombre, new Nodo(mensaje.getIpOrigen(), mensaje.getPuertoOrigen()));
            enviarA(nombre, "#ADM:CONFIRMAR_REGISTRO");
            enviarDirectorioATodos();
        } else if (contenido != null && contenido.startsWith("#ADM:BAJA:")) {
            String nombre = contenido.substring("#ADM:BAJA:".length());
            directorio.eliminar(nombre);
            enviarDirectorioATodos();
        }
    }

    private void enviarA(String nombre, String contenido) {
        Nodo destino = directorio.buscar(nombre);
        if (destino == null) return;
        IMensajeAdmon respuesta = fabrica.crearMensaje();
        respuesta.setRemitente("servidor");
        respuesta.setDestinatario(nombre);
        respuesta.setContenido(contenido);
        dispatcher.enviar(respuesta);
    }

    private void enviarDirectorioATodos() {
        for (String nombre : directorio.nombres()) {
            for (String otro : directorio.nombres()) {
                Nodo nodo = directorio.buscar(otro);
                if (nodo == null) continue;
                IMensajeAdmon aviso = fabrica.crearMensaje();
                aviso.setRemitente("servidor");
                aviso.setDestinatario(nombre);
                aviso.setContenido("#ADM:DIRECTORIO:" + otro + ":" + nodo.ip() + ":" + nodo.puerto());
                dispatcher.enviar(aviso);
            }
        }
    }
}
