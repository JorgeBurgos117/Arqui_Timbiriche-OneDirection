package modelo;

import java.util.List;
import java.util.Set;
import juego.objetosNegocio.Linea;
import juego.objetosNegocio.Punto;

public interface IModeloVista {

    void agregarObservador(IObservadorVista observador);

    void quitarObservador(IObservadorVista observador);

    GeometriaTablero getGeometria();

    List<JugadorVisual> getJugadores();

    JugadorVisual getJugadorEnTurno();

    List<LineaVisual> getLineas();

    List<CuadroVisual> getCuadros();

    Punto getSeleccion();

    Set<Punto> getCandidatos();

    Punto getHover();

    Linea getLineaPreview();

    PaletaColor getPaletaTurno();

    List<PaletaColor> getPaletasDisponibles();

    VotoPendiente getVotoPendiente();

    List<Aviso> getAvisos();

    boolean isPartidaTerminada();

    String getTextoFin();

    List<FilaResultado> getResultados();
}
