package mensajeria;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import red.api.IDispatcher;
import red.api.IFactoryRed;

// Único lugar que hace new de las clases de mensajería; la red solo se pide por IFactoryRed
public class FactoryMensajeria implements IFactoryMensajeria {

    public static final long INTERVALO_PING_MS = 2000;
    public static final Duration TIEMPO_VIDA = Duration.ofMillis(INTERVALO_PING_MS * 3);

    private final List<Runnable> paraDetener = new CopyOnWriteArrayList<>();

    @Override
    public IMensajeria crearMensajeriaCliente(IFactoryRed red, String miIp, int miPuerto, Nodo servidor) {
        BlockingQueue<IMensajeAdmon> colaSalida = new LinkedBlockingQueue<>();
        BlockingQueue<IMensajeAdmon> colaEntrada = new LinkedBlockingQueue<>();
        ISerializador serializador = new SerializadorJSON();
        IDirectorio directorio = new Directorio();
        directorio.registrar(IMensajeAdmon.SERVIDOR, servidor);

        int puerto = red.crearReceptor(miPuerto, new ReceptorMensajes(serializador, colaEntrada));
        IFabricaMensajes fabrica = new FabricaMensajes(miIp, puerto);
        DispatcherMensajes dispatcher = new DispatcherMensajes(directorio, colaSalida);

        PingAlive pingAlive = new PingAlive(fabrica, directorio, colaSalida, INTERVALO_PING_MS);
        IManejadorMensaje manejadorApp = new ManejadorAplicacion();
        IManejadorMensaje manejadorAdmon = new ManejadorAdministracion(pingAlive, directorio);

        arrancar(colaSalida, colaEntrada, serializador, red.crearDispatcher(), manejadorApp, manejadorAdmon);
        paraDetener.add(pingAlive::detener);
        return new Mensajeria(fabrica, dispatcher, List.of(manejadorApp, manejadorAdmon));
    }

    @Override
    public IMensajeria crearMensajeriaServidor(IFactoryRed red, String miIp, int miPuerto) {
        BlockingQueue<IMensajeAdmon> colaSalida = new LinkedBlockingQueue<>();
        BlockingQueue<IMensajeAdmon> colaEntrada = new LinkedBlockingQueue<>();
        ISerializador serializador = new SerializadorJSON();
        IDirectorio directorio = new Directorio();

        int puerto = red.crearReceptor(miPuerto, new ReceptorMensajes(serializador, colaEntrada));
        IFabricaMensajes fabrica = new FabricaMensajes(miIp, puerto);
        fabrica.setRemitente(IMensajeAdmon.SERVIDOR);
        DispatcherMensajes dispatcher = new DispatcherMensajes(directorio, colaSalida);

        TablaTiempos tabla = new TablaTiempos();
        IManejadorMensaje manejadorApp = new ManejadorAplicacion();
        IManejadorMensaje manejadorAdmon = new ManejadorAdmonServidor(directorio, fabrica, dispatcher, tabla);

        arrancar(colaSalida, colaEntrada, serializador, red.crearDispatcher(), manejadorApp, manejadorAdmon);
        VerificadorPingAlive verificador = new VerificadorPingAlive(tabla, TIEMPO_VIDA, directorio, fabrica, dispatcher);
        verificador.start();
        paraDetener.add(verificador::detener);
        return new Mensajeria(fabrica, dispatcher, List.of(manejadorApp, manejadorAdmon));
    }

    @Override
    public void detener() {
        paraDetener.forEach(Runnable::run);
        paraDetener.clear();
    }

    private void arrancar(BlockingQueue<IMensajeAdmon> colaSalida, BlockingQueue<IMensajeAdmon> colaEntrada,
                          ISerializador serializador, IDispatcher redDispatcher,
                          IManejadorMensaje manejadorApp, IManejadorMensaje manejadorAdmon) {
        Sender sender = new Sender(colaSalida, serializador, redDispatcher);
        ProcesadorMensajes procesador = new ProcesadorMensajes(colaEntrada, manejadorApp, manejadorAdmon);
        sender.start();
        procesador.start();
        paraDetener.add(sender::detener);
        paraDetener.add(procesador::detener);
    }
}
