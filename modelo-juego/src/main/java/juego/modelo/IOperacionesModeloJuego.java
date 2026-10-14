package juego.modelo;

import juego.objetosNegocio.Accion;
import juego.objetosNegocio.Coordenada;

public interface IOperacionesModeloJuego {

    void seleccionarPunto(int fila, int columna, String nombreJugador);
    void jugarLinea(int fila, int columna, String nombreJugador);
}
