package juego.modelo;

import juego.objetosNegocio.*;

import java.util.List;

public interface IConsultaModeloJuego {


    int getFilas();
    int getColumnas();
    List<Linea> getLineasMarcadas();
    List<Cuadro> getCuadros();
    PuntoSeleccionado getPuntoSeleccionado();
    Jugada getUltimaJugada();
    List<Jugador> getJugadores();
    Jugador getJugadorActual();
    EstadoPartida getEstadoPartida();
    String getMotivoRechazo();
    String getJugadorRechazado();

}
