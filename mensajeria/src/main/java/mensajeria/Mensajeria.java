package mensajeria;

import java.util.List;

public class Mensajeria implements IMensajeria {

    private final IFabricaMensajes fabrica;
    private final DispatcherMensajes dispatcher;
    private final String servidor = IMensajeAdmon.SERVIDOR;
    private final List<IManejadorMensaje> manejadores;

    public Mensajeria(IFabricaMensajes fabrica, DispatcherMensajes dispatcher, List<IManejadorMensaje> manejadores) {
        this.fabrica = fabrica;
        this.dispatcher = dispatcher;
        this.manejadores = List.copyOf(manejadores);
    }

    @Override
    public IMensajeApp crearMensaje() {
        IMensajeAdmon m = fabrica.crearMensaje();
        return m;
    }

    @Override
    public void enviar(IMensajeApp app) throws DestinoNoEncontradoException {
        if (IMensajeAdmon.esAdministrativo(app)) {
            throw new IllegalArgumentException("El contenido no puede empezar con " + IMensajeAdmon.PREFIJO);
        }
        IMensajeAdmon m = (IMensajeAdmon) app;
        dispatcher.enviar(m);
    }

    @Override
    public void registrar(String nombre) {
        fabrica.setRemitente(nombre);
        enviarAdministrativo(IMensajeAdmon.REGISTRO);
    }

    @Override
    public void darseDeBaja() {
        enviarAdministrativo(IMensajeAdmon.BAJA);
    }

    @Override
    public void setReceptor(IReceptorMensajes receptor) {
        manejadores.forEach(m -> m.setReceptor(receptor));
    }

    private void enviarAdministrativo(String contenido) {
        IMensajeAdmon m = fabrica.crearMensaje();
        m.setDestinatario(servidor);
        m.setContenido(contenido);
        dispatcher.enviar(m);
    }
}
