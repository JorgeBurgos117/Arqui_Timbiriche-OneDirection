package modelo;

import java.util.Map;

public interface IControlModelo {

    void validar(int x, int y);

    void moverCursor(int x, int y);

    void salirDelTablero();

    void cancelarSeleccion();

    boolean cambiarColores(Map<Integer, PaletaColor> paletas);

    void abandonar();

    void proponerFin();

    void votarFin(int idVotante, boolean acepta);
}
