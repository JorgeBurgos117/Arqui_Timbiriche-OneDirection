package juego.modelo;

import juego.objetosNegocio.Punto;

public interface IControlJuego {

    void validar(Punto punto, int idJugador);

    void cancelarSeleccion(int idJugador);

    void abandonar(int idJugador);

    void proponerFin(int idJugador);

    void votarFin(int idJugador, boolean acepta);

    void recibir(Accion accion);
}
