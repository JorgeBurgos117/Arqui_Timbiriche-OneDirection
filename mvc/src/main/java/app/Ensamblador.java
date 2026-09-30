package app;

import java.util.Random;
import controlador.ControlMenu;
import controlador.ControlVista;
import controlador.ILanzadorPartida;
import juego.dominio.TamanioTablero;
import juego.modelo.ColaAcciones;
import juego.modelo.ControlJuego;
import juego.modelo.DatosJuego;
import juego.red.BitacoraConsola;
import juego.red.PublicadorAcciones;
import modelo.ControlModelo;
import modelo.DatosVisuales;
import modelo.ModeloMenu;
import vista.juego.FrmJuego;
import vista.menu.FrmMenuPrincipal;
import vista.util.Styles;

public class Ensamblador implements ILanzadorPartida {

    private static final int ID_LOCAL = 1;
    private static final boolean TODOS_LOCALES = true;

    private final ModeloMenu modeloMenu = new ModeloMenu();
    private FrmMenuPrincipal menu;

    public void arrancar() {
        menu = new FrmMenuPrincipal(modeloMenu);
        menu.setControl(new ControlMenu(this));
        menu.mostrar();
    }

    @Override
    public void lanzar(int jugadores) {
        TamanioTablero tamanio = TamanioTablero.paraJugadores(jugadores);
        DatosJuego datosJuego = new DatosJuego(tamanio, modeloMenu.getNombresProvisionales(jugadores), new Random());
        ColaAcciones cola = new ColaAcciones();
        ControlJuego controlJuego = new ControlJuego(datosJuego, cola);

        PublicadorAcciones publicador = new PublicadorAcciones(cola);
        publicador.agregarDestino(new BitacoraConsola());
        publicador.iniciar();

        DatosVisuales datosVisuales = new DatosVisuales(datosJuego, ID_LOCAL, TODOS_LOCALES, Styles.LADO_AREA_TABLERO);
        datosJuego.agregarObservador(datosVisuales);
        ControlModelo controlModelo = new ControlModelo(datosVisuales, controlJuego);

        FrmJuego frmJuego = new FrmJuego(datosVisuales);
        datosVisuales.agregarObservador(frmJuego);

        ControlVista controlVista = new ControlVista(controlModelo, () -> {
            publicador.detener();
            datosJuego.quitarObservador(datosVisuales);
            datosVisuales.quitarObservador(frmJuego);
            frmJuego.dispose();
            menu.mostrar();
        });
        frmJuego.setControl(controlVista);

        menu.ocultar();
        frmJuego.mostrar();
    }
}
