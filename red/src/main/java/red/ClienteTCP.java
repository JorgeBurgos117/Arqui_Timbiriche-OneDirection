package red;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClienteTCP {

    public static final int TIMEOUT_CONEXION_MS = 3000;

    private final LengthFramer framer = new LengthFramer();

    // Una conexión por paquete: se abre, se manda y se cierra
    public void enviar(Paquete paquete) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(paquete.ip(), paquete.puerto()), TIMEOUT_CONEXION_MS);
            framer.frameMsg(paquete.mensaje().getBytes(StandardCharsets.UTF_8), socket.getOutputStream());
        }
    }
}
