package app;

import controlador.ControlEjercerTurno;
import controlador.ControlMenu;
import controlador.ControlOpcionesPartida;
import controlador.ILanzadorPartida;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
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
        FachadaModeloJuego fachadaJuego = new FachadaModeloJuego(new Partida(new Tablero(puntos, puntos), ordenTurnos));

        TransmisorRed transmisor = new TransmisorRed(fachadaJuego);
        fachadaJuego.agregarObservador(transmisor);
        transmisor.iniciar();

        int[] ventanasAbiertas = {registrados.size()};
        Runnable alCerrarVentana = () -> {
            ventanasAbiertas[0]--;
            if (ventanasAbiertas[0] == 0) {
                transmisor.detener();
                fachadaJuego.eliminarObservador(transmisor);
                menu.mostrar();
            }
        };

        int tamanoCelda = (Styles.LADO_AREA_TABLERO - 2 * Styles.MARGEN_TABLERO) / (puntos - 1);
        List<FrmJuego> ventanas = new ArrayList<>();
        for (Jugador jugador : registrados) {
            ventanas.add(ensamblarJugador(fachadaJuego, jugador.getNombre(), registrados, tamanoCelda, alCerrarVentana));
        }

        menu.ocultar();
        acomodar(ventanas);
        ventanas.forEach(FrmJuego::enfocarSiEsMiTurno);
    }

    private FrmJuego ensamblarJugador(FachadaModeloJuego fachadaJuego, String jugadorLocal, List<Jugador> registrados,
                                      int tamanoCelda, Runnable alCerrarVentana) {
        TraductorModeloJuego traductor = new TraductorModeloJuego(tamanoCelda, Styles.MARGEN_TABLERO);
        for (Jugador jugador : registrados) {
            traductor.asignarColor(jugador.getNombre(), PaletaColor.porNombre(jugador.getColor()).getColor());
        }
        ControlModeloPresentacion controlModelo =
                new ControlModeloPresentacion(fachadaJuego, fachadaJuego, traductor, jugadorLocal);
        ReceptorMovimientos receptor = new ReceptorMovimientos(fachadaJuego, traductor, jugadorLocal);
        fachadaJuego.agregarObservador(receptor);
        FachadaModeloPresentacion fachadaPresentacion = new FachadaModeloPresentacion(controlModelo, receptor);

        ControlEjercerTurno controlTurno = new ControlEjercerTurno();
        controlTurno.setModelo(fachadaPresentacion);
        ControlOpcionesPartida controlOpciones = new ControlOpcionesPartida();
        controlOpciones.setModelo(fachadaPresentacion);

        VistaTablero vistaTablero = new VistaTablero();
        vistaTablero.setModelo(fachadaPresentacion);
        vistaTablero.setControlador(controlTurno);
        VistaJugadores vistaJugadores = new VistaJugadores(jugadorLocal);
        vistaJugadores.setModelo(fachadaPresentacion);
        fachadaPresentacion.agregarObservador(vistaTablero);
        fachadaPresentacion.agregarObservador(vistaJugadores);
        receptor.sincronizar();

        FrmJuego frmJuego = new FrmJuego(Styles.TITULO_VENTANA + " — " + jugadorLocal,
                vistaTablero, vistaJugadores, fachadaPresentacion, controlTurno, controlOpciones);
        VistaResultados vistaResultados = new VistaResultados(controlOpciones::cerrarVentana);
        vistaResultados.setModelo(fachadaPresentacion);
        vistaResultados.setPropietario(frmJuego);
        fachadaPresentacion.agregarObservador(frmJuego);
        fachadaPresentacion.agregarObservador(vistaResultados);

        controlOpciones.setAlCerrar(() -> {
            fachadaJuego.eliminarObservador(receptor);
            frmJuego.dispose();
            alCerrarVentana.run();
        });
        return frmJuego;
    }

    private void acomodar(List<FrmJuego> ventanas) {
        Rectangle area = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int n = ventanas.size();
        ventanas.forEach(FrmJuego::preparar);
        int ancho = ventanas.get(0).getWidth();
        int alto = ventanas.get(0).getHeight();
        int pasoX = n > 1 ? Math.max(40, Math.min(ancho + 8, (area.width - ancho) / (n - 1))) : 0;
        int pasoY = n * ancho + (n - 1) * 8 <= area.width ? 0 : 30;
        int inicioX = area.x + Math.max(0, (area.width - (ancho + pasoX * (n - 1))) / 2);
        for (int i = 0; i < n; i++) {
            int y = area.y + Math.max(0, Math.min(i * pasoY, area.height - alto));
            ventanas.get(i).mostrar(inicioX + i * pasoX, y);
        }
    }
}
