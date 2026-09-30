package controlador;

public class ControlMenu implements IControlMenu {

    private final ILanzadorPartida lanzador;

    public ControlMenu(ILanzadorPartida lanzador) {
        this.lanzador = lanzador;
    }

    @Override
    public void iniciarPartida(int jugadores) {
        lanzador.lanzar(jugadores);
    }

    @Override
    public void salir() {
        System.exit(0);
    }
}
