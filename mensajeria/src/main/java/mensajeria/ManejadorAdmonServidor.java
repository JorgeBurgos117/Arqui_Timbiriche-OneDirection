package mensajeria;

public class ManejadorAdmonServidor implements IManejadorMensaje {

    private final IDirectorio directorio;
    private final IFabricaMensajes fabrica;
    private final DispatcherMensajes dispatcher;
    private final TablaTiempos tabla;

    public ManejadorAdmonServidor(IDirectorio directorio, IFabricaMensajes fabrica,
                                  DispatcherMensajes dispatcher, TablaTiempos tabla) {
        this.directorio = directorio;
        this.fabrica = fabrica;
        this.dispatcher = dispatcher;
        this.tabla = tabla;
    }

    @Override
    public void manejar(IMensajeAdmon mensaje) {
        String remitente = mensaje.getRemitente();
        if (remitente == null || remitente.isBlank() || remitente.equals(IMensajeAdmon.SERVIDOR)) {
            return;
        }
        switch (mensaje.getContenido()) {
            case IMensajeAdmon.REGISTRO -> {
                directorio.registrar(remitente, new Nodo(mensaje.getIpOrigen(), mensaje.getPuertoOrigen()));
                tabla.actualizar(remitente);
                responder(remitente, IMensajeAdmon.CONFIRMA_REGISTRO);
            }
            case IMensajeAdmon.BAJA -> {
                responder(remitente, IMensajeAdmon.CONFIRMA_BAJA);
                directorio.eliminar(remitente);
                tabla.eliminar(remitente);
            }
            case IMensajeAdmon.PING -> {
                if (directorio.nombres().contains(remitente)) {
                    tabla.actualizar(remitente);
                }
            }
            default -> {
            }
        }
    }

    private void responder(String destinatario, String contenido) {
        IMensajeAdmon respuesta = fabrica.crearMensaje();
        respuesta.setDestinatario(destinatario);
        respuesta.setContenido(contenido);
        dispatcher.enviar(respuesta);
    }
}
