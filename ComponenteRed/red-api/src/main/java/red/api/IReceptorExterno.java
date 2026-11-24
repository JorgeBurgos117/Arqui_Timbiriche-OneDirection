package red.api;

/**
 * Callback: lo IMPLEMENTA la aplicacion (la mensajeria) que usa el componente.
 * <p>
 * Cuando llega un texto por la red, el componente llama {@link #recibir(String)}.
 * Los textos se entregan uno por uno y en el orden en que llegaron, siempre desde el
 * mismo hilo del componente.
 * <p>
 * Esta interfaz es la que aplica la inversion de dependencias: la red no conoce a la
 * aplicacion, solo sabe que alguien implementa IReceptorExterno.
 */
public interface IReceptorExterno {

    /**
     * Recibe un texto que llego por la red, tal como lo mando el otro nodo.
     *
     * @param mensaje texto recibido (por ejemplo un JSON)
     */
    void recibir(String mensaje);
}
