package modeloJuego;

import java.util.List;
import modeloJuego.objetosNegocio.Cuadro;
import modeloJuego.objetosNegocio.EstadoPartida;
import modeloJuego.objetosNegocio.Jugada;
import modeloJuego.objetosNegocio.Jugador;
import modeloJuego.objetosNegocio.Linea;
import modeloJuego.objetosNegocio.MotivoFin;
import modeloJuego.objetosNegocio.PuntoSeleccionado;

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

    MotivoFin getMotivoFin();

    Jugador getGanador();

    List<Jugador> getPosiciones();

    Jugador getUltimoAbandono();

    Jugador getProponenteFin();

    List<Jugador> getPendientesFin();
}
