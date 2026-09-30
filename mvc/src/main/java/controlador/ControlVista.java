package controlador;

import java.util.Map;
import modelo.IControlModelo;
import modelo.PaletaColor;

public class ControlVista implements IControlVista {

    private final IControlModelo controlModelo;
    private final Runnable alSalir;
    private boolean salio = false;

    public ControlVista(IControlModelo controlModelo, Runnable alSalir) {
        this.controlModelo = controlModelo;
        this.alSalir = alSalir;
    }

    @Override
    public void clickTablero(int x, int y) {
        controlModelo.validar(x, y);
    }

    @Override
    public void moverCursor(int x, int y) {
        controlModelo.moverCursor(x, y);
    }

    @Override
    public void salirTablero() {
        controlModelo.salirDelTablero();
    }

    @Override
    public void cancelarSeleccion() {
        controlModelo.cancelarSeleccion();
    }

    @Override
    public boolean cambiarColores(Map<Integer, PaletaColor> paletas) {
        return controlModelo.cambiarColores(paletas);
    }

    @Override
    public void abandonar() {
        controlModelo.abandonar();
    }

    @Override
    public void proponerFin() {
        controlModelo.proponerFin();
    }

    @Override
    public void votarFin(int idVotante, boolean acepta) {
        controlModelo.votarFin(idVotante, acepta);
    }

    @Override
    public void salirAlMenu() {
        if (!salio) {
            salio = true;
            alSalir.run();
        }
    }
}
