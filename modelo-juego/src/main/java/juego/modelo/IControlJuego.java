package juego.modelo;

import juego.dominio.Accion;
import juego.dominio.Coordenada;

public interface IControlJuego {

    void validar(Coordenada punto, int idJugador);

    void cancelarSeleccion(int idJugador);

    void abandonar(int idJugador);

    void proponerFin(int idJugador);

    void votarFin(int idJugador, boolean acepta);

    void recibir(Accion accion);
}
