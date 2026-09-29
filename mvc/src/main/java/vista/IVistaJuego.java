package vista;

import java.util.List;
import modelo.Coordenada;
import controlador.IControlJuego;

/**
 * Lo que el control le puede ordenar a la vista.
 */
public interface IVistaJuego {

    void setControl(IControlJuego control);

    void mostrar();

    void setTurno(int idJugador);

    void setPuntos(int idJugador, int puntos);

    /** Marca el punto de origen y los puntos a los que se puede tirar linea. */
    void setSeleccion(Coordenada punto, List<Coordenada> candidatos);

    void limpiarSeleccion();

    void trazarLinea(Coordenada a, Coordenada b, int idJugador);

    /** La coordenada es la esquina superior izquierda del cuadro. */
    void pintarCuadro(Coordenada esquinaSuperiorIzquierda, int idJugador);

    void mostrarMensaje(String mensaje);
}