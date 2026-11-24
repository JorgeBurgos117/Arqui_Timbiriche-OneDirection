package mensajeria;

import mensajeria.api.IFabricaMensajes;
import mensajeria.api.IMensajeAdmon;
import mensajeria.api.IMensajeApp;
import mensajeria.api.IMensajeria;
import mensajeria.api.IReceptorMensajes;
import mensajeria.api.Nodo;

public class Mensajeria implements IMensajeria {
    private final IFabricaMensajes fabrica;
    private final DispatcherMensajes dispatcher;
    private final ReceptorMensajes receptor;
    private final ProcesadorMensajes procesador;
    private final ManejadorAplicacion manejadorApp;
    private final ManejadorAdministracion manejadorAdmon;
    private final Nodo servidor;
    private IReceptorMensajes receptorExterno;

    public Mensajeria(IFabricaMensajes fabrica, DispatcherMensajes dispatcher,
                      ReceptorMensajes receptor, ProcesadorMensajes procesador,
                      ManejadorAplicacion manejadorApp, ManejadorAdministracion manejadorAdmon,
                      Nodo servidor) {
        this.fabrica = fabrica;
        this.dispatcher = dispatcher;
        this.receptor = receptor;
        this.procesador = procesador;
        this.manejadorApp = manejadorApp;
        this.manejadorAdmon = manejadorAdmon;
        this.servidor = servidor;
    }

    @Override
    public IMensajeApp crearMensaje() {
        return (IMensajeApp) fabrica.crearMensaje();
    }

    @Override
    public void enviar(IMensajeApp app) {
        IMensajeAdmon administrativo = (IMensajeAdmon) app;
        dispatcher.enviar(administrativo);
    }

    @Override
    public void registrar(String nombre) {
        fabrica.setRemitente(nombre);
        IMensajeAdmon registro = fabrica.crearMensaje();
        registro.setDestinatario("servidor");
        registro.setContenido("#ADM:REGISTRAR:" + nombre);
        dispatcher.enviar(registro);
    }

    @Override
    public void darseDeBaja() {
        IMensajeAdmon baja = fabrica.crearMensaje();
        baja.setDestinatario(servidor == null ? "" : "servidor");
        baja.setContenido("#ADM:BAJA:" + baja.getRemitente());
        if (servidor != null) dispatcher.enviar(baja);
    }

    @Override
    public void setReceptor(IReceptorMensajes receptor) {
        this.receptorExterno = receptor;
        manejadorApp.setReceptor(receptor);
        if (manejadorAdmon != null) manejadorAdmon.setReceptor(receptor);
    }

    @Override
    public void detener() {
        procesador.detener();
        receptor.detener();
        dispatcher.detener();
    }
}
