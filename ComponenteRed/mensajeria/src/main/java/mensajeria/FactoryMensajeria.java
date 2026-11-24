package mensajeria;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import mensajeria.api.IFactoryMensajeria;
import mensajeria.api.IMensajeria;
import mensajeria.api.Nodo;
import red.api.IFactoryRed;

public class FactoryMensajeria implements IFactoryMensajeria {
    private final List<Mensajeria> mensajerias = new ArrayList<>();

    @Override
    public synchronized IMensajeria crearMensajeriaCliente(IFactoryRed red, String miIp,
                                                             int miPuerto, Nodo servidor) {
        FabricaMensajes fabrica = new FabricaMensajes(miIp, miPuerto);
        Directorio directorio = new Directorio();
        directorio.registrar("servidor", servidor);
        DispatcherMensajes dispatcher = new DispatcherMensajes(directorio, red.crearDispatcher());
        ReceptorMensajes receptor = new ReceptorMensajes();
        ManejadorAplicacion manejadorApp = new ManejadorAplicacion();
        ManejadorAdministracion manejadorAdmon = new ManejadorAdministracion(directorio);
        ProcesadorMensajes procesador = new ProcesadorMensajes(
                receptor.getColaEntrada(), manejadorApp, manejadorAdmon);
        try {
            red.crearReceptor(miPuerto, receptor);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo iniciar el receptor de mensajeria", e);
        }
        procesador.iniciar();
        Mensajeria mensajeria = new Mensajeria(fabrica, dispatcher, receptor, procesador,
                manejadorApp, manejadorAdmon, servidor);
        mensajerias.add(mensajeria);
        return mensajeria;
    }

    @Override
    public synchronized IMensajeria crearMensajeriaServidor(IFactoryRed red, String miIp,
                                                              int miPuerto) {
        FabricaMensajes fabrica = new FabricaMensajes(miIp, miPuerto);
        Directorio directorio = new Directorio();
        directorio.registrar("servidor", new Nodo(miIp, miPuerto));
        DispatcherMensajes dispatcher = new DispatcherMensajes(directorio, red.crearDispatcher());
        ReceptorMensajes receptor = new ReceptorMensajes();
        ManejadorAplicacion manejadorApp = new ManejadorAplicacion();
        ManejadorAdmonServidor manejadorAdmon = new ManejadorAdmonServidor(
                directorio, fabrica, dispatcher);
        ProcesadorMensajes procesador = new ProcesadorMensajes(
                receptor.getColaEntrada(), manejadorApp, manejadorAdmon);
        try {
            red.crearReceptor(miPuerto, receptor);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo iniciar el receptor de mensajeria", e);
        }
        procesador.iniciar();
        Mensajeria mensajeria = new Mensajeria(fabrica, dispatcher, receptor, procesador,
                manejadorApp, null, null);
        mensajerias.add(mensajeria);
        return mensajeria;
    }

    @Override
    public synchronized void detener() {
        mensajerias.forEach(Mensajeria::detener);
        mensajerias.clear();
    }
}
