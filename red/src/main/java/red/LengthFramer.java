package red;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// Cada mensaje viaja como [longitud int de 4 bytes][bytes del mensaje]
public class LengthFramer {

    public static final int LONGITUD_MAXIMA = 1 << 20;

    private final DataInputStream entrada;

    public LengthFramer() {
        this.entrada = null;
    }

    public LengthFramer(InputStream entrada) {
        this.entrada = new DataInputStream(entrada);
    }

    public void frameMsg(byte[] mensaje, OutputStream salida) throws IOException {
        if (mensaje.length > LONGITUD_MAXIMA) {
            throw new IOException("Mensaje demasiado largo: " + mensaje.length + " bytes");
        }
        DataOutputStream out = new DataOutputStream(salida);
        out.writeInt(mensaje.length);
        out.write(mensaje);
        out.flush();
    }

    public byte[] nextMsg() throws IOException {
        int longitud;
        try {
            longitud = entrada.readInt();
        } catch (EOFException fin) {
            return null;
        }
        if (longitud < 0 || longitud > LONGITUD_MAXIMA) {
            throw new IOException("Longitud inválida: " + longitud);
        }
        byte[] mensaje = new byte[longitud];
        entrada.readFully(mensaje);
        return mensaje;
    }
}
