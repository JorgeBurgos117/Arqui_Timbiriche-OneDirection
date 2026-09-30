package modelo;

import java.util.List;
import java.util.Set;
import juego.dominio.Arista;
import juego.dominio.Coordenada;

public interface IModeloVista {

    void agregarObservador(IObservadorVista observador);

    void quitarObservador(IObservadorVista observador);

    GeometriaTablero getGeometria();

    List<JugadorVisual> getJugadores();

    JugadorVisual getJugadorEnTurno();

    List<LineaVisual> getLineas();

    List<CuadroVisual> getCuadros();

    Coordenada getSeleccion();

    Set<Coordenada> getCandidatos();

    Coordenada getHover();

    Arista getLineaPreview();

    PaletaColor getPaletaTurno();

    List<PaletaColor> getPaletasDisponibles();

    VotoPendiente getVotoPendiente();

    List<Aviso> getAvisos();

    boolean isPartidaTerminada();

    String getTextoFin();

    List<FilaResultado> getResultados();
}
