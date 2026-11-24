package red;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Escucha en un puerto y recibe los textos que mandan los otros nodos.
 * <pre>
 *   red --Socket--> [longitud][bytes] --LengthFramer--> bytes --UTF-8--> String --> Receptor
 * </pre>
 * Tiene su propio hilo porque {@code accept()} se queda bloqueado esperando
 * conexiones. Atiende las conexiones una tras otra (no en paralelo) para respetar el
 * orden de llegada; como cada conexion es corta, esto no es un problema. A cada
 * conexion le pone un tiempo limite de lectura para que un cliente lento o caido no
 * bloquee a los demas.
 */
public class ServidorTCP {

    private static final Logger LOG = Logger.getLogger(ServidorTCP.class.getName());

    /** Tiempo maximo esperando datos de una conexion ya abierta (ms). */
    public static final int TIMEOUT_LECTURA_MS = 5000;

    private int puerto;
    private Receptor receptor;
    private ServerSocket servidor;
    private Thread hilo;

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public void setReceptor(Receptor receptor) {
        this.receptor = receptor;
    }

    /** Puerto real en el que escucha (util si se pidio el puerto 0 = cualquiera libre). */
    public int getPuerto() {
        return servidor == null ? puerto : servidor.getLocalPort();
    }

    /**
     * Abre el puerto aqui mismo (para que un error como "puerto ocupado" se detecte
     * al arrancar) y luego acepta conexiones en otro hilo.
     */
    public synchronized void iniciar() throws IOException {
        if (receptor == null) {
            throw new IllegalStateException("No se ha configurado el receptor. Use setReceptor() primero.");
        }
        if (servidor != null) {
            return;
        }
        servidor = new ServerSocket();
        servidor.setReuseAddress(true);
        servidor.bind(new InetSocketAddress(puerto));
        ServerSocket ss = servidor;
        hilo = new Thread(() -> aceptarConexiones(ss), "ServidorTCP-" + getPuerto());
        hilo.setDaemon(true);
        hilo.start();
    }

    /** Deja de escuchar y libera el puerto. */
    public synchronized void detener() {
        if (servidor == null) {
            return;
        }
        try {
            servidor.close(); // hace que accept() lance SocketException y el hilo termine
        } catch (IOException e) {
            LOG.log(Level.FINE, "Error al cerrar el servidor", e);
        }
        try {
            hilo.join(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        servidor = null;
    }

    private void aceptarConexiones(ServerSocket ss) {
        while (!ss.isClosed()) {
            try (Socket conexion = ss.accept()) {
                conexion.setSoTimeout(TIMEOUT_LECTURA_MS);
                atender(conexion);
            } catch (SocketException e) {
                if (!ss.isClosed()) {
                    LOG.log(Level.WARNING, "Error de conexion en el puerto " + ss.getLocalPort(), e);
                }
            } catch (IOException e) {
                LOG.log(Level.WARNING, "Mensaje incompleto en el puerto {0}: {1}",
                        new Object[]{ss.getLocalPort(), e.getMessage()});
            }
        }
    }

    /** Lee todos los mensajes que mande esa conexion (normalmente uno) y los encola. */
    private void atender(Socket conexion) throws IOException {
        LengthFramer framer = new LengthFramer(conexion.getInputStream());
        byte[] bytes;
        while ((bytes = framer.nextMsg()) != null) {
            receptor.encolar(new String(bytes, StandardCharsets.UTF_8));   // bytes -> String
        }
    }
}
