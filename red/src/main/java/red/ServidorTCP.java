package red;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServidorTCP {

    public static final int TIMEOUT_LECTURA_MS = 5000;

    private int puerto;
    private Receptor receptor;
    private ServerSocket servidor;
    private Thread hilo;

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public int getPuerto() {
        return servidor != null ? servidor.getLocalPort() : puerto;
    }

    public void setReceptor(Receptor receptor) {
        this.receptor = receptor;
    }

    public void iniciar() {
        try {
            servidor = new ServerSocket(puerto);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo abrir el puerto " + puerto, e);
        }
        ServerSocket ss = servidor;
        hilo = new Thread(() -> aceptarConexiones(ss), "red-servidor-" + ss.getLocalPort());
        hilo.setDaemon(true);
        hilo.start();
    }

    public void detener() {
        try {
            if (servidor != null) {
                servidor.close();
            }
        } catch (IOException ignored) {
        }
    }

    private void aceptarConexiones(ServerSocket ss) {
        while (!ss.isClosed()) {
            try (Socket conexion = ss.accept()) {
                atender(conexion);
            } catch (IOException e) {
                if (!ss.isClosed()) {
                    System.err.println("[red] Error atendiendo conexión: " + e.getMessage());
                }
            }
        }
    }

    private void atender(Socket conexion) throws IOException {
        conexion.setSoTimeout(TIMEOUT_LECTURA_MS);
        LengthFramer framer = new LengthFramer(conexion.getInputStream());
        byte[] mensaje;
        while ((mensaje = framer.nextMsg()) != null) {
            receptor.encolar(new String(mensaje, StandardCharsets.UTF_8));
        }
    }
}
