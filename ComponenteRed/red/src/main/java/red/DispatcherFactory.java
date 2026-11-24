package red;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import red.api.IDispatcher;
import red.api.IFactoryRed;
import red.api.IReceptorExterno;

/**
 * Implementacion de {@link IFactoryRed}: crea las clases concretas del componente,
 * las conecta y las arranca. La aplicacion solo recibe {@link IDispatcher} y solo entrega su callback
 * {@link IReceptorExterno}; nunca toca Dispatcher, Receptor ni sockets.
 * <pre>
 *   IFactoryRed red = new DispatcherFactory();
 *   IDispatcher dispatcher = red.crearDispatcher();
 *   red.crearReceptor(5001, texto -&gt; ...);           // empieza a escuchar
 *   dispatcher.enviar("{...json...}", "localhost", 5002);
 *   ...
 *   red.detener();                                      // al cerrar la aplicacion
 * </pre>
 */
public class DispatcherFactory implements IFactoryRed {

    private final List<Dispatcher> dispatchers = new ArrayList<>();
    private final List<ServidorTCP> servidores = new ArrayList<>();
    private final List<Receptor> receptores = new ArrayList<>();

    /** Crea y arranca la cola de salida. */
    @Override
    public synchronized IDispatcher crearDispatcher() {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setClienteTCP(new ClienteTCP());
        dispatcher.iniciar();
        dispatchers.add(dispatcher);
        return dispatcher;
    }

    /**
     * Empieza a escuchar en el puerto y entrega cada texto recibido a {@code externo}.
     *
     * @param puerto  puerto donde escuchar (0 = cualquiera libre)
     * @param externo callback de la aplicacion
     * @return el puerto real en el que quedo escuchando
     * @throws IOException si el puerto esta ocupado o no se puede abrir
     */
    @Override
    public synchronized int crearReceptor(int puerto, IReceptorExterno externo) throws IOException {
        Receptor receptor = new Receptor();
        receptor.setReceptorExterno(externo);
        receptor.iniciar();

        ServidorTCP servidorTCP = new ServidorTCP();
        servidorTCP.setPuerto(puerto);
        servidorTCP.setReceptor(receptor);
        try {
            servidorTCP.iniciar();
        } catch (IOException e) {
            receptor.detener();
            throw e;
        }
        receptores.add(receptor);
        servidores.add(servidorTCP);
        return servidorTCP.getPuerto();
    }

    /**
     * Apaga todo lo que creo esta fabrica: primero deja de escuchar, luego termina de
     * mandar lo pendiente y por ultimo entrega lo que ya habia llegado.
     */
    @Override
    public synchronized void detener() {
        servidores.forEach(ServidorTCP::detener);
        dispatchers.forEach(Dispatcher::detener);
        receptores.forEach(Receptor::detener);
        servidores.clear();
        dispatchers.clear();
        receptores.clear();
    }
}
