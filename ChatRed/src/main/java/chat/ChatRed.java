package chat;

import java.util.Scanner;
import mensajeria.api.IMensajeApp;
import mensajeria.api.IMensajeria;
import mensajeria.api.IReceptorMensajes;

/**
 * Aplicacion de chat por consola sobre el componente de mensajeria.
 */
public class ChatRed implements IReceptorMensajes {

    private final IMensajeria mensajeria;
    private final String miNombre;
    private final boolean registrarAlIniciar;
    private String destinatarioActivo;

    /**
     * @param mensajeria componente de mensajeria
     * @param miNombre nombre visible del usuario
     */
    public ChatRed(IMensajeria mensajeria, String miNombre) {
        this(mensajeria, miNombre, true);
    }

    public ChatRed(IMensajeria mensajeria, String miNombre, boolean registrarAlIniciar) {
        this.mensajeria = mensajeria;
        this.miNombre = miNombre;
        this.registrarAlIniciar = registrarAlIniciar;
        mensajeria.setReceptor(this);
    }

    /**
     * Callback: lo llama el hilo "Receptor" del componente de red cada vez que llega un
     * mensaje, uno por uno y en orden de llegada.
     */
    @Override
    public void recibir(IMensajeApp mensaje) {
        String remitente = mensaje.getRemitente();
        if (remitente == null || remitente.isBlank()) {
            remitente = "Remitente desconocido";
        }
        System.out.println("\r<< " + remitente + ": " + mensaje.getContenido());
        System.out.print("> ");
    }

    @Override
    public void estadoBuzon(boolean activo) {
        if (activo) {
            System.out.println("Buzon activo. Puedes enviar mensajes.");
            System.out.print("> ");
        }
    }

    /**
     * Lee lo que escribe el jugador y lo manda al otro, hasta que escriba "salir".
     *
     * @param teclado entrada de la consola
     */
    public void conversar(Scanner teclado) {
        if (registrarAlIniciar) mensajeria.registrar(miNombre);
        System.out.println("Escribe @Nombre mensaje para enviar. 'salir' para terminar.");
        System.out.print("> ");
        while (teclado.hasNextLine()) {
            String texto = teclado.nextLine();
            if (texto.equalsIgnoreCase("salir")) {
                break;
            }
            if (!texto.isBlank()) {
                String contenido = texto;
                if (texto.startsWith("@")) {
                    int espacio = texto.indexOf(' ');
                    if (espacio > 1) {
                        destinatarioActivo = texto.substring(1, espacio);
                        contenido = texto.substring(espacio + 1);
                    }
                }
                if (destinatarioActivo == null) {
                    System.out.println("Primero indica el destinatario: @Nombre mensaje");
                } else if (!contenido.isBlank()) {
                    IMensajeApp mensaje = mensajeria.crearMensaje();
                    mensaje.setDestinatario(destinatarioActivo);
                    mensaje.setContenido(contenido);
                    mensajeria.enviar(mensaje);
                    System.out.println(">> " + contenido);
                }
            }
            System.out.print("> ");
        }
    }
}
