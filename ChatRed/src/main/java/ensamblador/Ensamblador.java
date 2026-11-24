package ensamblador;

import chat.ChatRed;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import mensajeria.FactoryMensajeria;
import mensajeria.api.IFactoryMensajeria;
import mensajeria.api.IMensajeria;
import mensajeria.api.Nodo;
import red.DispatcherFactory;
import red.api.IFactoryRed;

/**
 * Ensamblador de la aplicacion completa.
 * <p>
 * Es el unico lugar que conoce la implementacion del componente de red
 * ({@code new DispatcherFactory()}). Pide la configuracion, crea el componente,
 * crea la aplicacion {@link ChatRed} y los conecta:
 * <ol>
 *   <li>crea la fabrica de red ({@link IFactoryRed});</li>
 *   <li>crea el componente de mensajeria y se lo da al chat;</li>
 *   <li>registra a la aplicacion como callback ({@code crearReceptor}) en su puerto;</li>
 *   <li>arranca la conversacion y, al terminar, apaga la red.</li>
 * </ol>).
 */
public class Ensamblador {

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        System.out.println("=== Chat de prueba del componente de red (OneDirection) ===");
        System.out.println("IPs de esta computadora: " + ipsLocales());

        // 1. Configuracion
        int miPuerto = Integer.parseInt(arg(args, 0, teclado, "Mi puerto", "5001"));
        String ipOtro = arg(args, 1, teclado, "IP del otro jugador", "localhost");
        int puertoOtro = Integer.parseInt(arg(args, 2, teclado, "Puerto del otro jugador",
                miPuerto == 5001 ? "5002" : "5001"));
        String miNombre = arg(args, 3, teclado, "Mi nombre", "Jugador" + miPuerto);
        String rol = arg(args, 4, teclado, "Rol (servidor/cliente)",
                miPuerto == 5001 ? "servidor" : "cliente");

        // 2. Componente de red 
        IFactoryRed red = new DispatcherFactory();
        IFactoryMensajeria fabricaMensajeria = new FactoryMensajeria();
        IMensajeria mensajeria;
        if ("servidor".equalsIgnoreCase(rol)) {
            mensajeria = fabricaMensajeria.crearMensajeriaServidor(red,
                    "127.0.0.1", miPuerto);
        } else {
            mensajeria = fabricaMensajeria.crearMensajeriaCliente(red,
                    "127.0.0.1", miPuerto, new Nodo(ipOtro, puertoOtro));
        }
        ChatRed chat = new ChatRed(mensajeria, miNombre,
                !"servidor".equalsIgnoreCase(rol));
        Runtime.getRuntime().addShutdownHook(new Thread(red::detener));

        System.out.println("Rol " + rol + " escuchando en el puerto " + miPuerto);

        // 4. Ejecutar y apagar
        chat.conversar(teclado);
        red.detener();
        System.out.println("Adios.");
    }

    /** Toma el valor de los argumentos o lo pregunta (Enter = valor por defecto). */
    private static String arg(String[] args, int i, Scanner teclado, String pregunta, String porDefecto) {
        if (args.length > i && !args[i].isBlank()) {
            return args[i];
        }
        System.out.print(pregunta + " [" + porDefecto + "]: ");
        String linea = teclado.hasNextLine() ? teclado.nextLine().trim() : "";
        return linea.isEmpty() ? porDefecto : linea;
    }

    /** IPv4 de las tarjetas de red activas, para saber que IP darle al otro jugador. */
    private static List<String> ipsLocales() {
        List<String> ips = new ArrayList<>();
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (ni.isUp() && !ni.isLoopback()) {
                    for (InetAddress dir : Collections.list(ni.getInetAddresses())) {
                        if (dir instanceof Inet4Address) {
                            ips.add(dir.getHostAddress());
                        }
                    }
                }
            }
        } catch (SocketException e) {
        }
        return ips;
    }
}
