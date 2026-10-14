package modeloPresentacion;

import dtos.FiguraDTO;
import dtos.JugadorDTO;
import dtos.ResultadoDTO;
import dtos.VotacionDTO;
import java.util.List;

public interface IConsultaModeloPresentacion {

    List<FiguraDTO> getFiguras();

    List<JugadorDTO> getJugadores();

    String getMensajeAdvertencia();

    boolean esMiTurno();

    VotacionDTO getVotacionPendiente();

    List<ResultadoDTO> getResultados();

    String getTextoFin();

    int getTamanoCelda();

    int getLadoTablero();
}
