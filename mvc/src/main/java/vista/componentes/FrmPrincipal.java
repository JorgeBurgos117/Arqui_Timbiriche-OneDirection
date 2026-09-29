package vista.componentes;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import controlador.IControlJuego;
import modelo.Coordenada;
import modelo.DatosJugador;
import vista.IVistaJuego;
import vista.PnlContenido;
import vista.componentes.DlgOpciones;
import vista.util.Styles;
import vista.componentes.PnlJugador;
import vista.componentes.tablero.PnlTablero;


public class FrmPrincipal extends JFrame implements IVistaJuego {

    private final PnlContenido pnlContenido;
    private final PnlTablero pnlTablero;
    private final Map<Integer, DatosJugador> jugadoresPorId = new LinkedHashMap<>();

    private IControlJuego control;

    public FrmPrincipal(List<DatosJugador> jugadores, int idLocal, int filas, int columnas) {
        super(Styles.TITULO_VENTANA);
        jugadores.forEach(j -> jugadoresPorId.put(j.id(), j));

        pnlContenido = new PnlContenido(jugadores, idLocal, filas, columnas, this::abrirOpciones);
        pnlTablero = pnlContenido.getPnlTablero();

        setContentPane(pnlContenido);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(Styles.ANCHO_MINIMO_VENTANA, Styles.ALTO_MINIMO_VENTANA));
        registrarEscape();
    }

    private void registrarEscape() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "cancelarSeleccion");
        getRootPane().getActionMap().put("cancelarSeleccion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (control != null) {
                    control.seleccionCancelada();
                }
            }
        });
    }

    private void abrirOpciones() {
        new DlgOpciones(this, control).setVisible(true);
    }

    // ------------------------------------------------------------- IVistaJuego
    @Override
    public void setControl(IControlJuego control) {
        this.control = control;
        pnlTablero.setControl(control);
    }

    @Override
    public void mostrar() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void setTurno(int idJugador) {
        pnlContenido.getPanelesJugador()
                .forEach((id, panel) -> panel.setEnTurno(id == idJugador));
        DatosJugador jugador = jugadoresPorId.get(idJugador);
        if (jugador != null) {
            pnlTablero.setPaletaTurno(jugador.paleta());
        }
    }

    @Override
    public void setPuntos(int idJugador, int puntos) {
        PnlJugador panel = pnlContenido.getPanelesJugador().get(idJugador);
        if (panel != null) {
            panel.setPuntos(puntos);
        }
    }

    @Override
    public void setSeleccion(Coordenada punto, List<Coordenada> candidatos) {
        pnlTablero.setSeleccion(punto, candidatos);
    }

    @Override
    public void limpiarSeleccion() {
        pnlTablero.limpiarSeleccion();
    }

    @Override
    public void trazarLinea(Coordenada a, Coordenada b, int idJugador) {
        DatosJugador jugador = jugadoresPorId.get(idJugador);
        if (jugador != null) {
            pnlTablero.trazarLinea(a, b, jugador.paleta());
        }
    }

    @Override
    public void pintarCuadro(Coordenada esquinaSuperiorIzquierda, int idJugador) {
        DatosJugador jugador = jugadoresPorId.get(idJugador);
        if (jugador != null) {
            pnlTablero.pintarCuadro(esquinaSuperiorIzquierda, jugador.paleta(), jugador.inicial());
        }
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        System.out.println("[VISTA] " + mensaje);
    }
}