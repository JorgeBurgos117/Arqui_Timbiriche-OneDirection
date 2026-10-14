package juego.modelo;

import juego.objetosNegocio.EstadoJuego;

public interface IConsultaModeloJuego {

    EstadoJuego get();

    void getFilas();
    void getColumnas();
    List<Linea> getLineasMarcadas();
    List<Fila> getFilas();
    List<Cuadro> getCuadros();
    PuntoSeleccionado getPuntoSeleccionado();
    Jugada getUltimaJugada();
    List<Jugadores> getJugadores();
    Jugador getJugadorActual();
    EstadoPartida getEstadoPartida();
    String getMotivoRechazo();
    String getJugadorRechazado();

}
