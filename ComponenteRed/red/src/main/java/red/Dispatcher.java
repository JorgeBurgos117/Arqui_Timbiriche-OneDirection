package red;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import red.api.IDispatcher;

/**
 * Cola de SALIDA.
 * <p>
 * {@link #enviar(String, String, int)} solo mete el envio a la cola y regresa de
 * inmediato. Un unico hilo ("Dispatcher") saca los envios uno por uno, en orden, y
 * los manda con el {@link ClienteTCP}. Si un envio falla se reintenta unas veces; si
 * sigue fallando se descarta ese envio y se continua con los demas.
 */
public class Dispatcher implements IDispatcher {

    private static final Logger LOG = Logger.getLogger(Dispatcher.class.getName());

    /** Envio especial para despertar al hilo y detenerlo. */
    private static final Paquete FIN = new Paquete("", "", 0);

    private final BlockingQueue<Paquete> cola = new LinkedBlockingQueue<>();
    private ClienteTCP clienteTCP;
    private int reintentos = 3;
    private long esperaEntreReintentosMs = 300;
    private Thread hilo;
    private volatile boolean activo;

    public void setClienteTCP(ClienteTCP clienteTCP) {
        this.clienteTCP = clienteTCP;
    }

    /** Cuantas veces se reintenta un envio fallido (0 = no reintentar). */
    public void setReintentos(int reintentos) {
        this.reintentos = Math.max(0, reintentos);
    }

    public void setEsperaEntreReintentosMs(long ms) {
        this.esperaEntreReintentosMs = Math.max(0, ms);
    }

    @Override
    public void enviar(String mensaje, String ip, int puerto) {
        if (mensaje == null) {
            throw new IllegalArgumentException("El mensaje no puede ser nulo");
        }
        if (ip == null || ip.isBlank() || puerto <= 0 || puerto > 65535) {
            throw new IllegalArgumentException("Destino invalido: " + ip + ":" + puerto);
        }
        if (!activo) {
            throw new IllegalStateException("El Dispatcher no esta iniciado");
        }
        cola.offer(new Paquete(mensaje, ip, puerto));
    }

    /** Arranca el hilo que vacia la cola de salida. */
    public synchronized void iniciar() {
        if (clienteTCP == null) {
            throw new IllegalStateException("No se ha configurado el cliente TCP. Use setClienteTCP() primero.");
        }
        if (activo) {
            return;
        }
        activo = true;
        hilo = new Thread(this::procesarCola, "Dispatcher");
        hilo.setDaemon(true);
        hilo.start();
    }

    /** Termina de mandar lo que ya estaba en la cola y detiene el hilo. */
    public synchronized void detener() {
        if (!activo) {
            return;
        }
        activo = false;
        cola.offer(FIN);
        try {
            hilo.join(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void procesarCola() {
        try {
            while (true) {
                Paquete paquete = cola.take();      // se bloquea hasta que haya algo
                if (paquete == FIN) {
                    return;
                }
                enviarConReintentos(paquete);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void enviarConReintentos(Paquete paquete) throws InterruptedException {
        for (int intento = 0; ; intento++) {
            try {
                clienteTCP.enviar(paquete);
                return;
            } catch (IOException | RuntimeException e) {
                if (intento >= reintentos) {
                    LOG.log(Level.WARNING, "No se pudo enviar a {0}: {1}", new Object[]{paquete, e.getMessage()});
                    return;
                }
                Thread.sleep(esperaEntreReintentosMs);
            }
        }
    }
}
