package red;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import red.api.IReceptorExterno;

/**
 * Cola de ENTRADA.
 * <p>
 * El {@link ServidorTCP} mete aqui cada texto que llega. Un unico hilo ("Receptor")
 * los saca uno por uno, en orden de llegada, y se los entrega a la aplicacion por el
 * callback {@link IReceptorExterno#recibir(String)}.
 * <p>
 * Por esto no se usa Observer: con un solo hilo y una cola, dos mensajes nunca se
 * procesan al mismo tiempo ni en desorden.
 */
public class Receptor {

    private static final Logger LOG = Logger.getLogger(Receptor.class.getName());
    /** Marca especial para detener el hilo (se compara por referencia). */
    private static final String FIN = new String("FIN");

    private final BlockingQueue<String> cola = new LinkedBlockingQueue<>();
    private IReceptorExterno receptorExterno;
    private Thread hilo;
    private volatile boolean activo;

    public void setReceptorExterno(IReceptorExterno receptorExterno) {
        this.receptorExterno = receptorExterno;
    }

    /** Lo llama el ServidorTCP (desde su hilo) cuando llega un mensaje. */
    public void encolar(String mensaje) {
        if (mensaje != null && activo) {
            cola.offer(mensaje);
        }
    }

    /** Arranca el hilo que entrega los mensajes. */
    public synchronized void iniciar() {
        if (receptorExterno == null) {
            throw new IllegalStateException("No se ha configurado el receptor externo. Use setReceptorExterno() primero.");
        }
        if (activo) {
            return;
        }
        activo = true;
        hilo = new Thread(this::procesarCola, "Receptor");
        hilo.setDaemon(true);
        hilo.start();
    }

    /** Entrega lo que ya estaba en la cola y detiene el hilo. */
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

    @SuppressWarnings("StringEquality")
    private void procesarCola() {
        try {
            while (true) {
                String mensaje = cola.take();
                if (mensaje == FIN) {
                    return;
                }
                try {
                    receptorExterno.recibir(mensaje);   // callback hacia la aplicacion
                } catch (RuntimeException e) {
                    // un error de la aplicacion no debe detener la entrega de los demas mensajes
                    LOG.log(Level.WARNING, "Error al entregar [" + mensaje + "]", e);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
