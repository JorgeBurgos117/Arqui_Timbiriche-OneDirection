package modeloJuego;

public interface IOperacionesModeloJuego {

    void seleccionarPunto(int fila, int columna, String nombreJugador);

    void jugarLinea(int fila, int columna, String nombreJugador);

    void cancelarSeleccion(String nombreJugador);

    void abandonar(String nombreJugador);

    void proponerFin(String nombreJugador);

    void votarFin(String nombreJugador, boolean acepta);
}
