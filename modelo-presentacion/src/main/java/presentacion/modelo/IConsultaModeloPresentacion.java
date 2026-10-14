package presentacion.modelo;

import presentacion.dtos.FiguraDTO;
import presentacion.dtos.JugadorDTO;

import java.util.List;

public interface IConsultaModeloPresentacion {

    List<FiguraDTO> getFiguras();
    List<JugadorDTO> getJugadores();
    String getMensajeAdvertencia();
    boolean esMiTurno();
    void agregarObservador(IObservadorPresentacion observador);
    void eliminarObservador(IObservadorPresentacion observador);
}
