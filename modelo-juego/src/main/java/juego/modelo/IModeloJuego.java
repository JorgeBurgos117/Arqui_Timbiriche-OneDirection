package juego.modelo;

public interface IModeloJuego {

    EstadoJuego get();

    void agregarObservador(IObservadorJuego observador);

    void quitarObservador(IObservadorJuego observador);
}
