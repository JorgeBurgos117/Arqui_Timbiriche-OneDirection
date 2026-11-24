package red;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class LengthFramerTest {

    @Test
    void separaVariosMensajesSeguidosEnElMismoFlujo() throws IOException {
        byte[] a = "primero".getBytes(StandardCharsets.UTF_8);
        byte[] b = "{\"fila\":2,\n\"columna\":3}".getBytes(StandardCharsets.UTF_8);
        byte[] c = new byte[0];

        ByteArrayOutputStream cable = new ByteArrayOutputStream();
        LengthFramer emisor = new LengthFramer(null);
        emisor.frameMsg(a, cable);
        emisor.frameMsg(b, cable);
        emisor.frameMsg(c, cable);

        LengthFramer receptor = new LengthFramer(new ByteArrayInputStream(cable.toByteArray()));
        assertArrayEquals(a, receptor.nextMsg());
        assertArrayEquals(b, receptor.nextMsg());
        assertArrayEquals(c, receptor.nextMsg());
        assertNull(receptor.nextMsg()); // fin limpio del flujo
    }

    @Test
    void mensajeCortadoALaMitadSeDetecta() throws IOException {
        ByteArrayOutputStream cable = new ByteArrayOutputStream();
        new LengthFramer(null).frameMsg("mensaje completo".getBytes(StandardCharsets.UTF_8), cable);
        byte[] cortado = Arrays.copyOf(cable.toByteArray(), 8);
        LengthFramer receptor = new LengthFramer(new ByteArrayInputStream(cortado));
        assertThrows(EOFException.class, receptor::nextMsg);
    }

    @Test
    void rechazaMensajesDemasiadoGrandes() {
        byte[] enorme = new byte[LengthFramer.LONGITUD_MAXIMA + 1];
        assertThrows(IOException.class, () -> new LengthFramer(null).frameMsg(enorme, new ByteArrayOutputStream()));
    }
}
