package red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import red.api.IDispatcher;

/**
 * Pruebas de punta a punta en la misma computadora: dos "nodos" en puertos distintos
 * que se mandan texto por TCP real usando solo las interfaces del componente.
 */
class ComponenteRedTest {

    private final DispatcherFactory nodoA = new DispatcherFactory();
    private final DispatcherFactory nodoB = new DispatcherFactory();

    @AfterEach
    void apagar() {
        nodoA.detener();
        nodoB.detener();
    }

    @Test
    void losMensajesLleganCompletosYEnOrden() throws Exception {
        int total = 200;
        List<String> recibidos = Collections.synchronizedList(new ArrayList<>());
        List<String> hilos = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch llegaron = new CountDownLatch(total);

        int puertoB = nodoB.crearReceptor(0, texto -> {
            recibidos.add(texto);
            hilos.add(Thread.currentThread().getName());
            llegaron.countDown();
        });
        IDispatcher dispatcherA = nodoA.crearDispatcher();

        for (int i = 0; i < total; i++) {
            dispatcherA.enviar("{\"n\":" + i + ",\"jugador\":\"Peña\"}", "localhost", puertoB);
        }

        assertTrue(llegaron.await(20, TimeUnit.SECONDS), "No llegaron todos los mensajes");
        for (int i = 0; i < total; i++) {
            assertEquals("{\"n\":" + i + ",\"jugador\":\"Peña\"}", recibidos.get(i), "orden o contenido incorrecto");
        }
        // todos se entregaron desde el mismo hilo del componente (no en paralelo)
        assertTrue(hilos.stream().allMatch("Receptor"::equals));
    }

    @Test
    void conversacionEnAmbosSentidosConPuertosDistintos() throws Exception {
        CountDownLatch respuesta = new CountDownLatch(1);
        List<String> recibidoEnA = new ArrayList<>();
        IDispatcher dispatcherB = nodoB.crearDispatcher();

        int puertoA = nodoA.crearReceptor(0, texto -> {
            recibidoEnA.add(texto);
            respuesta.countDown();
        });
        // B contesta a A (en una prueba real, la mensajeria sabria el puerto por el directorio)
        int puertoB = nodoB.crearReceptor(0, texto -> dispatcherB.enviar("pong a " + texto, "localhost", puertoA));

        nodoA.crearDispatcher().enviar("ping", "localhost", puertoB);

        assertTrue(respuesta.await(10, TimeUnit.SECONDS));
        assertEquals(List.of("pong a ping"), recibidoEnA);
    }

    @Test
    void unDestinoCaidoNoDetieneLosDemasEnvios() throws Exception {
        int puertoCerrado;
        try (ServerSocket s = new ServerSocket(0)) {
            puertoCerrado = s.getLocalPort(); // se cierra: nadie escucha ahi
        }
        CountDownLatch llego = new CountDownLatch(1);
        int puertoB = nodoB.crearReceptor(0, texto -> llego.countDown());

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setClienteTCP(new ClienteTCP());
        dispatcher.setReintentos(1);
        dispatcher.setEsperaEntreReintentosMs(10);
        dispatcher.iniciar();
        try {
            dispatcher.enviar("a nadie", "localhost", puertoCerrado);
            dispatcher.enviar("a B", "localhost", puertoB);
            assertTrue(llego.await(10, TimeUnit.SECONDS), "El mensaje para B no llego");
        } finally {
            dispatcher.detener();
        }
    }

    @Test
    void validaElDestinoAlEnviar() {
        IDispatcher dispatcher = nodoA.crearDispatcher();
        assertThrows(IllegalArgumentException.class, () -> dispatcher.enviar(null, "localhost", 5000));
        assertThrows(IllegalArgumentException.class, () -> dispatcher.enviar("x", null, 5000));
        assertThrows(IllegalArgumentException.class, () -> dispatcher.enviar("x", "localhost", 0));
    }

    @Test
    void puertoOcupadoSeDetectaAlArrancar() throws Exception {
        try (ServerSocket ocupado = new ServerSocket(0)) {
            assertThrows(IOException.class, () -> nodoA.crearReceptor(ocupado.getLocalPort(), texto -> { }));
        }
    }

    @Test
    void unErrorDeLaAplicacionNoDetieneLaEntrega() throws Exception {
        CountDownLatch segundo = new CountDownLatch(1);
        int puertoB = nodoB.crearReceptor(0, texto -> {
            if (texto.equals("falla")) {
                throw new RuntimeException("error de la app");
            }
            segundo.countDown();
        });
        IDispatcher d = nodoA.crearDispatcher();
        d.enviar("falla", "localhost", puertoB);
        d.enviar("ok", "localhost", puertoB);
        assertTrue(segundo.await(10, TimeUnit.SECONDS));
    }
}
