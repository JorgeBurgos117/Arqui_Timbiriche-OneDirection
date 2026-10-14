package app;

import controlador.ControlEjercerTurno;
import controlador.ControlMenu;
import controlador.ControlOpcionesPartida;
import controlador.ILanzadorPartida;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import modeloJuego.FachadaModeloJuego;
import modeloJuego.objetosNegocio.Jugador;
import modeloJuego.objetosNegocio.Partida;
import modeloJuego.objetosNegocio.Tablero;
import modeloJuego.objetosNegocio.TamanioTablero;
import modeloPresentacion.ControlModeloPresentacion;
import modeloPresentacion.FachadaModeloPresentacion;
import modeloPresentacion.ModeloMenu;
import modeloPresentacion.ReceptorMovimientos;
import modeloPresentacion.TraductorModeloJuego;
import red.TransmisorRed;
import vista.FrmJuego;
import vista.VistaJugadores;
import vista.VistaResultados;
import vista.VistaTablero;
import vista.menu.FrmMenuPrincipal;
import vista.util.PaletaColor;
import vista.util.Styles;

public class Ensamblador implements ILanzadorPartida {

    // null = todos los jugadores comparten esta ventana; con red sera el nombre del jugador de este equipo.
    private static final String JUGADOR_LOCAL = null;

    private final ModeloMenu modeloMenu = new ModeloMenu();
    private final Random azar = new Random();
    private FrmMenuPrincipal menu;

    public void arrancar() {
        menu = new FrmMenuPrincipal(modeloMenu);
        menu.setControl(new ControlMenu(this, modeloMenu));
        menu.mostrar();
    }

    @Override
    public void ensamblar(List<String> nombres) {
        List<Jugador> registrados = new ArrayList<>();
        for (int i = 0; i < nombres.size(); i++) {
            String color = PaletaColor.DISPONIBLES.get(i).getNombre();
            registrados.add(new Jugador(nombres.get(i), "avatar" + (i + 1), color));
        }
        List<Jugador> ordenTurnos = new ArrayList<>(registrados);
        Collections.shuffle(ordenTurnos, azar);

        int puntos = TamanioTablero.paraJugadores(nombres.size()).getPuntosPorLado();
        Partida partida = new Partida(new Tablero(puntos, puntos), ordenTurnos);
        FachadaModeloJuego fachadaJuego = new FachadaModeloJuego(partida);

        TransmisorRed transmisor = new TransmisorRed(fachadaJuego);
        fachadaJuego.agregarObservador(transmisor);
        transmisor.iniciar();

        int tamanoCelda = (Styles.LADO_AREA_TABLERO - 2 * Styles.MARGEN_TABLERO) / (puntos - 1);
        TraductorModeloJuego traductor = new TraductorModeloJuego(tamanoCelda, Styles.MARGEN_TABLERO);
        for (Jugador jugador : registrados) {
            traductor.asignarColor(jugador.getNombre(), PaletaColor.porNombre(jugador.getColor()).getColor());
        }
        ControlModeloPresentacion controlModelo =
                new ControlModeloPresentacion(fachadaJuego, fachadaJuego, traductor, JUGADOR_LOCAL);
        ReceptorMovimientos receptor = new ReceptorMovimientos(fachadaJuego, traductor, JUGADOR_LOCAL);
        fachadaJuego.agregarObservador(receptor);
        FachadaModeloPresentacion fachadaPresentacion = new FachadaModeloPresentacion(controlModelo, receptor);

        ControlEjercerTurno controlTurno = new ControlEjercerTurno();
        controlTurno.setModelo(fachadaPresentacion);
        ControlOpcionesPartida controlOpciones = new ControlOpcionesPartida();
        controlOpciones.setModelo(fachadaPresentacion);

        VistaTablero vistaTablero = new VistaTablero();
        vistaTablero.setModelo(fachadaPresentacion);
        vistaTablero.setControlador(controlTurno);
        VistaJugadores vistaJugadores = new VistaJugadores(JUGADOR_LOCAL);
        vistaJugadores.setModelo(fachadaPresentacion);
        fachadaPresentacion.agregarObservador(vistaTablero);
        fachadaPresentacion.agregarObservador(vistaJugadores);
        receptor.sincronizar();

        FrmJuego frmJuego = new FrmJuego(vistaTablero, vistaJugadores, fachadaPresentacion, controlTurno, controlOpciones);
        VistaResultados vistaResultados = new VistaResultados(controlOpciones::salirAlMenu);
        vistaResultados.setModelo(fachadaPresentacion);
        vistaResultados.setPropietario(frmJuego);
        fachadaPresentacion.agregarObservador(frmJuego);
        fachadaPresentacion.agregarObservador(vistaResultados);

        controlOpciones.setAlSalir(() -> {
            transmisor.detener();
            fachadaJuego.eliminarObservador(transmisor);
            fachadaJuego.eliminarObservador(receptor);
            frmJuego.dispose();
            menu.mostrar();
        });

        menu.ocultar();
        frmJuego.mostrar();
    }
}
