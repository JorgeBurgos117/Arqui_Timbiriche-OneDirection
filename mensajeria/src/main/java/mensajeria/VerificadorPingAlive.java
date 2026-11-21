package mensajeria;

import java.time.Duration;

public class VerificadorPingAlive extends Thread {

    private final TablaTiempos tabla;
    private final Duration tiempoVida;
    private final IDirectorio directorio;
    private final IFabricaMensajes fabrica;
    private final DispatcherMensajes dispatcher;

    public VerificadorPingAlive(TablaTiempos tabla, Duration tiempoVida, IDirectorio directorio,
                                IFabricaMensajes fabrica, DispatcherMensajes dispatcher) {
        super("mensajeria-verificador");
        setDaemon(true);
        this.tabla = tabla;
        this.tiempoVida = tiempoVida;
        this.directorio = directorio;
        this.fabrica = fabrica;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        long revisarCadaMs = Math.max(100, tiempoVida.toMillis() / 2);
        while (!isInterrupted()) {
            try {
                Thread.sleep(revisarCadaMs);
            } catch (InterruptedException e) {
                return;
            }
            for (String vencido : tabla.vencidos(tiempoVida)) {
                tabla.eliminar(vencido);
                directorio.eliminar(vencido);
                avisarATodos(vencido);
            }
        }
    }

    public void detener() {
        interrupt();
    }

    private void avisarATodos(String vencido) {
        for (String nombre : directorio.nombres()) {
            IMensajeAdmon aviso = fabrica.crearMensaje();
            aviso.setDestinatario(nombre);
            aviso.setContenido(IMensajeAdmon.ELIMINAR + vencido);
            try {
                dispatcher.enviar(aviso);
            } catch (DestinoNoEncontradoException ignorado) {
            }
        }
    }
}
