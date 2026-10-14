package juego.modelo;

import juego.objetosNegocio.EstadoJuego;

public interface IModeloJuego {

    EstadoJuego get();

    void agregarObservador(IObservadorJuego observador);

    void quitarObservador(IObservadorJuego observador);
}
