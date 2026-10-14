package modeloPresentacion;

import dtos.FiguraDTO;
import dtos.JugadorDTO;
import dtos.ResultadoDTO;
import dtos.VotacionDTO;
import java.awt.Color;
import java.util.List;
import java.util.Map;

public class FachadaModeloPresentacion implements IConsultaModeloPresentacion, IOperacionesModeloPresentacion {

    private final ControlModeloPresentacion control;
    private final ReceptorMovimientos receptor;

    public FachadaModeloPresentacion(ControlModeloPresentacion control, ReceptorMovimientos receptor) {
        this.control = control;
        this.receptor = receptor;
    }

    @Override
    public void seleccionarPunto(int x, int y) {
        control.seleccionarPunto(x, y);
    }

    @Override
    public void cancelarSeleccion() {
        control.cancelarSeleccion();
    }

    @Override
    public boolean cambiarColores(Map<String, Color> colores) {
        boolean aplicado = control.cambiarColores(colores);
        if (aplicado) {
            receptor.sincronizar();
        }
        return aplicado;
    }

    @Override
    public void abandonar() {
        control.abandonar();
    }

    @Override
    public void proponerFin() {
        control.proponerFin();
    }

    @Override
    public void votarFin(String votante, boolean acepta) {
        control.votarFin(votante, acepta);
    }

    @Override
    public List<FiguraDTO> getFiguras() {
        return receptor.sacarFiguras();
    }

    @Override
    public List<JugadorDTO> getJugadores() {
        return receptor.sacarJugadores();
    }

    @Override
    public String getMensajeAdvertencia() {
        return receptor.getMensajeAdvertencia();
    }

    @Override
    public boolean esMiTurno() {
        return control.esMiTurno();
    }

    @Override
    public VotacionDTO getVotacionPendiente() {
        return receptor.getVotacionPendiente();
    }

    @Override
    public List<ResultadoDTO> getResultados() {
        return receptor.getResultados();
    }

    @Override
    public String getTextoFin() {
        return receptor.getTextoFin();
    }

    @Override
    public int getTamanoCelda() {
        return receptor.getTamanoCelda();
    }

    @Override
    public int getLadoTablero() {
        return receptor.getLadoTablero();
    }

    public void agregarObservador(ObservadorPresentacion observador) {
        receptor.agregarObservador(observador);
    }

    public void eliminarObservador(ObservadorPresentacion observador) {
        receptor.eliminarObservador(observador);
    }
}
