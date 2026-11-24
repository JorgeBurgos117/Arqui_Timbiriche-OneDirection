package red.api;

import java.io.IOException;

/**
 * Fabrica del componente de red, vista como interfaz.
 * <p>
 * Asi el ensamblador (quien arma la aplicacion) solo depende de interfaces de
 * red-api. La unica linea que conoce la implementacion es la que crea la fabrica:
 * <pre>
 *   IFactoryRed red = new DispatcherFactory();
 * </pre>
 */
public interface IFactoryRed {

    /**
     * Crea y arranca la cola de salida.
     *
     * @return el dispatcher para enviar textos
     */
    IDispatcher crearDispatcher();

    /**
     * Empieza a escuchar en el puerto y entrega cada texto recibido a {@code externo}.
     *
     * @param puerto  puerto donde escuchar (0 = cualquiera libre)
     * @param externo callback de la aplicacion
     * @return el puerto real en el que quedo escuchando
     * @throws IOException si el puerto esta ocupado o no se puede abrir
     */
    int crearReceptor(int puerto, IReceptorExterno externo) throws IOException;

    /** Apaga todo lo que creo la fabrica (al cerrar la aplicacion). */
    void detener();
}
