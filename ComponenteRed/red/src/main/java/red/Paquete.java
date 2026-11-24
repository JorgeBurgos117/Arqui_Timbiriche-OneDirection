package red;

/**
 * Un envio pendiente dentro de la cola del {@link Dispatcher}: el texto junto con a
 * donde hay que mandarlo. Es interno de la red; por el cable solo viaja el texto.
 *
 * @param mensaje texto a enviar
 * @param ip      IP del destino
 * @param puerto  puerto del destino
 */
public record Paquete(String mensaje, String ip, int puerto) {

    @Override
    public String toString() {
        return ip + ":" + puerto + " [" + mensaje + "]";
    }
}
