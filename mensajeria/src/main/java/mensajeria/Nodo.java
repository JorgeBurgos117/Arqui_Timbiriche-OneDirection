package mensajeria;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public record Nodo(String ip, int puerto) {

    // Pregunta al sistema qué interfaz local usaría para llegar a ipDestino (UDP no manda nada)
    public static String ipLocalHacia(String ipDestino) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName(ipDestino), 9);
            return socket.getLocalAddress().getHostAddress();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo determinar la IP local hacia " + ipDestino, e);
        }
    }
}
