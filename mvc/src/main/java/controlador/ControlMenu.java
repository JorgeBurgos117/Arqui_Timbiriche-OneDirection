package controlador;

import modeloPresentacion.ModeloMenu;

public class ControlMenu {

    private final ILanzadorPartida lanzador;
    private final ModeloMenu modelo;

    public ControlMenu(ILanzadorPartida lanzador, ModeloMenu modelo) {
        this.lanzador = lanzador;
        this.modelo = modelo;
    }

    public void iniciarPartida(int jugadores) {
        lanzador.ensamblar(modelo.getNombresProvisionales(jugadores));
    }

    public void salir() {
        System.exit(0);
    }
}
