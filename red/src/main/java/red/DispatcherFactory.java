package red;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import red.api.IDispatcher;
import red.api.IFactoryRed;
import red.api.IReceptorExterno;

public class DispatcherFactory implements IFactoryRed {

    private final List<Dispatcher> dispatchers = new CopyOnWriteArrayList<>();
    private final List<ServidorTCP> servidores = new CopyOnWriteArrayList<>();
    private final List<Receptor> receptores = new CopyOnWriteArrayList<>();

    @Override
    public IDispatcher crearDispatcher() {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setClienteTCP(new ClienteTCP());
        dispatcher.iniciar();
        dispatchers.add(dispatcher);
        return dispatcher;
    }

    @Override
    public int crearReceptor(int puerto, IReceptorExterno externo) {
        Receptor receptor = new Receptor();
        receptor.setReceptorExterno(externo);
        receptor.iniciar();

        ServidorTCP servidor = new ServidorTCP();
        servidor.setPuerto(puerto);
        servidor.setReceptor(receptor);
        servidor.iniciar();

        receptores.add(receptor);
        servidores.add(servidor);
        return servidor.getPuerto();
    }

    @Override
    public void detener() {
        servidores.forEach(ServidorTCP::detener);
        receptores.forEach(Receptor::detener);
        dispatchers.forEach(Dispatcher::detener);
        servidores.clear();
        receptores.clear();
        dispatchers.clear();
    }
}
