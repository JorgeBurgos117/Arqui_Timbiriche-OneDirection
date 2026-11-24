package red;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Manda un texto a otra computadora con una conexion "intermitente":
 * abre el socket, manda el texto y lo cierra.
 * <pre>
 *   String --UTF-8--> bytes --LengthFramer--> [longitud][bytes] --Socket--> red
 * </pre>
 * Solo lo usa el hilo del {@link Dispatcher}; nunca el hilo de la aplicacion.
 */
public class ClienteTCP {

    /** Tiempo maximo para conectarse con el otro nodo (ms). */
    public static final int TIMEOUT_CONEXION_MS = 3000;

    /**
     * @param paquete texto y destino (ip + puerto)
     * @throws IOException si no se pudo conectar o escribir
     */
    public void enviar(Paquete paquete) throws IOException {
        byte[] bytes = paquete.mensaje().getBytes(StandardCharsets.UTF_8);   // String -> bytes
        try (Socket socket = new Socket()) {
            // connect con limite de tiempo para no quedarse colgado si el destino no existe
            socket.connect(new InetSocketAddress(paquete.ip(), paquete.puerto()), TIMEOUT_CONEXION_MS);
            new LengthFramer(null).frameMsg(bytes, socket.getOutputStream());
            socket.shutdownOutput(); // avisa al servidor que ya no hay mas datos
        }
    }
}
