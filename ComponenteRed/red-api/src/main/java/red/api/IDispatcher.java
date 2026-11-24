package red.api;

/**
 * Lo que la aplicacion (la mensajeria) usa para MANDAR un texto a otra computadora.
 * <p>
 * La red no sabe que hay dentro del texto (normalmente sera un JSON armado por la
 * mensajeria); solo lo lleva a {@code ip:puerto}.
 * <p>
 * {@link #enviar(String, String, int)} solo forma el envio en la cola de salida y
 * regresa de inmediato; un hilo del componente lo manda despues. Asi quien llama
 * nunca se queda congelado esperando a la red.
 */
public interface IDispatcher {

    /**
     * Encola un texto para mandarlo a otra computadora.
     *
     * @param mensaje texto a enviar (por ejemplo un JSON)
     * @param ip      IP del destino (o "localhost" para pruebas en una sola computadora)
     * @param puerto  puerto donde escucha el destino; permite tener varios nodos en la
     *                misma computadora durante las pruebas
     * @throws IllegalArgumentException si el mensaje es nulo o el destino no es valido
     */
    void enviar(String mensaje, String ip, int puerto);
}
