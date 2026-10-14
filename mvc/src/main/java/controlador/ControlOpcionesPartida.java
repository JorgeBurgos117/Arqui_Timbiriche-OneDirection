package controlador;

import java.awt.Color;
import java.util.Map;
import modeloPresentacion.IOperacionesModeloPresentacion;

public class ControlOpcionesPartida {

    private IOperacionesModeloPresentacion modelo;
    private Runnable alSalir;
    private boolean salio = false;

    public void setModelo(IOperacionesModeloPresentacion modelo) {
        this.modelo = modelo;
    }

    public void setAlSalir(Runnable alSalir) {
        this.alSalir = alSalir;
    }

    public boolean cambiarColores(Map<String, Color> colores) {
        return modelo != null && modelo.cambiarColores(colores);
    }

    public void abandonar() {
        if (modelo != null) {
            modelo.abandonar();
        }
    }

    public void proponerFin() {
        if (modelo != null) {
            modelo.proponerFin();
        }
    }

    public void votarFin(String votante, boolean acepta) {
        if (modelo != null) {
            modelo.votarFin(votante, acepta);
        }
    }

    public void salirAlMenu() {
        if (!salio && alSalir != null) {
            salio = true;
            alSalir.run();
        }
    }
}
