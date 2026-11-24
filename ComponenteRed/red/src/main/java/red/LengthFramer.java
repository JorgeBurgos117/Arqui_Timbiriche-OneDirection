package red;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Framing: como sabe el receptor donde termina cada mensaje.
 * <p>
 * TCP entrega un "chorro" de bytes sin separaciones. Por eso, antes de cada mensaje
 * se mandan 4 bytes con su tamano:
 * <pre>
 *   [ 4 bytes: longitud N ][ N bytes: mensaje ]
 * </pre>
 * El receptor lee primero la longitud y despues exactamente N bytes. Si la conexion
 * se corta a la mitad, se detecta (no se procesa un mensaje incompleto).
 * <p>
 * Adaptado de LengthFramer del libro "TCP/IP Sockets in Java" (Calvert y Donahoo),
 * seccion 3.3. El libro usa 2 bytes (maximo 65,535); aqui se usan 4 bytes.
 */
public class LengthFramer {

    /** Tamano maximo aceptado por mensaje: 1 MB. */
    public static final int LONGITUD_MAXIMA = 1024 * 1024;

    private final DataInputStream entrada;

    /**
     * @param entrada flujo del que se leeran los mensajes (null si solo se va a enviar)
     */
    public LengthFramer(InputStream entrada) {
        this.entrada = entrada == null ? null : new DataInputStream(entrada);
    }

    /** Escribe [longitud][mensaje] en el flujo de salida. */
    public void frameMsg(byte[] mensaje, OutputStream salida) throws IOException {
        if (mensaje.length > LONGITUD_MAXIMA) {
            throw new IOException("Mensaje demasiado grande: " + mensaje.length + " bytes");
        }
        DataOutputStream out = new DataOutputStream(salida);
        out.writeInt(mensaje.length);   // prefijo de longitud (4 bytes)
        out.write(mensaje);
        out.flush();
    }

    /**
     * Lee el siguiente mensaje completo.
     *
     * @return los bytes del mensaje, o null si el otro lado cerro sin mandar mas
     * @throws IOException si la conexion se corta a la mitad de un mensaje
     */
    public byte[] nextMsg() throws IOException {
        if (entrada == null) {
            throw new IllegalStateException("Este framer no tiene flujo de entrada");
        }
        int longitud;
        try {
            longitud = entrada.readInt();
        } catch (EOFException e) {
            return null;
        }
        if (longitud < 0 || longitud > LONGITUD_MAXIMA) {
            throw new IOException("Longitud de mensaje invalida: " + longitud);
        }
        byte[] mensaje = new byte[longitud];
        entrada.readFully(mensaje); // si se corta a la mitad lanza EOFException
        return mensaje;
    }
}
