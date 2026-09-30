package controlador;

import java.util.Map;
import modelo.PaletaColor;

public interface IControlVista {

    void clickTablero(int x, int y);

    void moverCursor(int x, int y);

    void salirTablero();

    void cancelarSeleccion();

    boolean cambiarColores(Map<Integer, PaletaColor> paletas);

    void abandonar();

    void proponerFin();

    void votarFin(int idVotante, boolean acepta);

    void salirAlMenu();
}
