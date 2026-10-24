package app;

import controlador.ControlEjercerTurno;
import java.util.ArrayList;
import java.util.List;
import juego.modelo.FachadaModeloJuego;
import juego.objetosNegocio.Jugador;
import juego.objetosNegocio.Partida;
import juego.objetosNegocio.Tablero;
import presentacion.modelo.ControlModeloPresentacion;
import presentacion.modelo.FachadaModeloPresentacion;
import presentacion.modelo.PaletaColor;
import presentacion.modelo.ReceptorMovimientos;
import presentacion.modelo.TraductorModeloJuego;
import vista.juego.FrmJuego;
import vista.juego.VistaJugadores;
import vista.juego.VistaTablero;
import vista.util.Styles;

public class Ensamblador {

    // Valores fijos mientras no existe el caso de uso del menú
    private static final List<String> NOMBRES = List.of("Danna", "Regina", "Mariana");
    private static final int PUNTOS_POR_LADO = 20;
    private static final int MARGEN = 20;

    public void ensamblar() {
        // Modelo de Juego (uno solo)
        List<Jugador> jugadores = new ArrayList<>();
        for (int i = 0; i < NOMBRES.size(); i++) {
            jugadores.add(new Jugador(i + 1, NOMBRES.get(i), null));
        }
        Tablero tablero = new Tablero(PUNTOS_POR_LADO, PUNTOS_POR_LADO);
        FachadaModeloJuego modeloJuego = new FachadaModeloJuego(new Partida(tablero, jugadores));

        // Tamaño de cada celda para que el tablero quepa en el área de la ventana
        int tamanoCelda = (Styles.LADO_AREA_TABLERO - 2 * MARGEN) / (PUNTOS_POR_LADO - 1);
        int ladoTablero = 2 * MARGEN + tamanoCelda * (PUNTOS_POR_LADO - 1);

        List<ReceptorMovimientos> receptores = new ArrayList<>();

        // Por cada jugador
        for (int i = 0; i < jugadores.size(); i++) {
            String jugadorLocal = jugadores.get(i).getNombre();

            // Modelo de Presentación
            TraductorModeloJuego traductor = new TraductorModeloJuego(tamanoCelda, MARGEN);
            for (int k = 0; k < jugadores.size(); k++) {          // todos con los mismos colores
                traductor.asignarColor(jugadores.get(k).getId(), PaletaColor.DISPONIBLES.get(k));
            }
            ReceptorMovimientos receptor = new ReceptorMovimientos(modeloJuego, traductor, jugadorLocal);
            modeloJuego.agregarObservador(receptor);              // el receptor observa al juego
            ControlModeloPresentacion controlMP =
                    new ControlModeloPresentacion(modeloJuego, modeloJuego, traductor, jugadorLocal);
            FachadaModeloPresentacion modeloPresentacion = new FachadaModeloPresentacion(controlMP, receptor);

            // Controlador y vistas
            ControlEjercerTurno control = new ControlEjercerTurno(modeloPresentacion);
            VistaTablero vistaTablero = new VistaTablero(modeloPresentacion, ladoTablero, tamanoCelda);
            vistaTablero.setControlador(control);
            VistaJugadores vistaJugadores = new VistaJugadores(modeloPresentacion);
            modeloPresentacion.agregarObservador(vistaTablero);   // las vistas observan al MP
            modeloPresentacion.agregarObservador(vistaJugadores);

            new FrmJuego(jugadorLocal, vistaTablero, vistaJugadores).mostrar(i);
            receptores.add(receptor);
        }

        // Estado inicial: puntos en gris y jugadores
        for (ReceptorMovimientos receptor : receptores) {
            receptor.cargarEstadoInicial();
        }
    }
}
