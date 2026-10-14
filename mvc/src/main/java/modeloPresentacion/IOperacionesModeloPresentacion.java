package modeloPresentacion;

import java.awt.Color;
import java.util.Map;

public interface IOperacionesModeloPresentacion {

    void seleccionarPunto(int x, int y);

    void cancelarSeleccion();

    boolean cambiarColores(Map<String, Color> colores);

    void abandonar();

    void proponerFin();

    void votarFin(String votante, boolean acepta);
}
