package controlador;

import modeloPresentacion.IOperacionesModeloPresentacion;

public class ControlEjercerTurno {

    private IOperacionesModeloPresentacion modelo;

    public void setModelo(IOperacionesModeloPresentacion modelo) {
        this.modelo = modelo;
    }

    public void seleccionarPunto(int x, int y) {
        if (modelo != null) {
            modelo.seleccionarPunto(x, y);
        }
    }

    public void cancelarSeleccion() {
        if (modelo != null) {
            modelo.cancelarSeleccion();
        }
    }
}
