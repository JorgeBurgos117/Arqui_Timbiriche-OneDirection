package juego.modelo;

import juego.dominio.EstadoJuego;

public interface IModeloJuego {

    EstadoJuego get();

    void agregarObservador(IObservadorJuego observador);

    void quitarObservador(IObservadorJuego observador);
}
