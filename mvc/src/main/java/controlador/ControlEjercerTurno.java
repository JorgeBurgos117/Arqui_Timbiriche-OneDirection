package controlador;

import presentacion.modelo.IOperacionesModeloPresentacion;

public class ControlEjercerTurno {

    private IOperacionesModeloPresentacion modelo;

    public ControlEjercerTurno(IOperacionesModeloPresentacion modelo) {
        this.modelo = modelo;
    }

    public void setModelo(IOperacionesModeloPresentacion modelo) {
        this.modelo = modelo;
    }

    public void seleccionarPunto(int x, int y) {
        modelo.seleccionarPunto(x, y);
    }
}
