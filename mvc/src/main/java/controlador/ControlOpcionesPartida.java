package controlador;

import java.awt.Color;
import java.util.Map;
import modeloPresentacion.IOperacionesModeloPresentacion;

public class ControlOpcionesPartida {

    private IOperacionesModeloPresentacion modelo;
    private Runnable alCerrar;
    private boolean cerrada = false;

    public void setModelo(IOperacionesModeloPresentacion modelo) {
        this.modelo = modelo;
    }

    public void setAlCerrar(Runnable alCerrar) {
        this.alCerrar = alCerrar;
    }

    public boolean cambiarColores(Map<String, Color> colores) {
        return modelo != null && modelo.cambiarColores(colores);
    }

    public void abandonar() {
        cerrarVentana();
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

    public void cerrarVentana() {
        if (!cerrada && alCerrar != null) {
            cerrada = true;
            alCerrar.run();
        }
    }
}
