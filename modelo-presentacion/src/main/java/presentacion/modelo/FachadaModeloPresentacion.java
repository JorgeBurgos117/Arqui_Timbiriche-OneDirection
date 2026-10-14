package presentacion.modelo;

import java.util.List;
import presentacion.dtos.FiguraDTO;
import presentacion.dtos.JugadorDTO;

public class FachadaModeloPresentacion
        implements IOperacionesModeloPresentacion, IConsultaModeloPresentacion {

    private final ControlModeloPresentacion control;
    private final ReceptorMovimientos receptor;

    public FachadaModeloPresentacion(ControlModeloPresentacion control, ReceptorMovimientos receptor) {
        this.control = control;
        this.receptor = receptor;
    }

    // Operaciones (vista -> modelo)
    @Override
    public void seleccionarPunto(int x, int y) {
        control.seleccionarPunto(x, y);
    }

    // Consultas
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

    // Observadores (las vistas se suscriben aquí, no al receptor)
    public void agregarObservador(IObservadorPresentacion o)  {
        receptor.agregarObservador(o);
    }
    public void eliminarObservador(IObservadorPresentacion o) {
        receptor.eliminarObservador(o);
    }
}