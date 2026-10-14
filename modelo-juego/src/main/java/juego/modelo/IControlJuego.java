package juego.modelo;

import juego.objetosNegocio.Accion;
import juego.objetosNegocio.Coordenada;

public interface IControlJuego {

    void validar(Coordenada punto, int idJugador);

    void cancelarSeleccion(int idJugador);

    void abandonar(int idJugador);

    void proponerFin(int idJugador);

    void votarFin(int idJugador, boolean acepta);

    void recibir(Accion accion);
}
