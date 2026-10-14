package vista;

import controlador.ControlOpcionesPartida;
import dtos.JugadorDTO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import vista.componentes.BtnRedondeado;
import vista.util.Dialogos;
import vista.util.PaletaColor;
import vista.util.Styles;

public class DlgOpciones extends JDialog {

    private final Window propietario;
    private final VistaJugadores vistaJugadores;
    private final ControlOpcionesPartida control;
    private final JugadorDTO jugador;

    public DlgOpciones(Window propietario, VistaJugadores vistaJugadores, ControlOpcionesPartida control) {
        super(propietario, Styles.TITULO_DLG_OPCIONES, ModalityType.DOCUMENT_MODAL);
        this.propietario = propietario;
        this.vistaJugadores = vistaJugadores;
        this.control = control;
        this.jugador = vistaJugadores.getJugadorLocal();
        construir();
    }

    private void construir() {
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Styles.COLOR_FONDO);
        int m = Styles.MARGEN_DLG;
        contenido.setBorder(BorderFactory.createEmptyBorder(m, m, m, m));

        if (jugador != null) {
            JLabel lblJugador = new JLabel("Jugador: " + jugador.getNombre());
            lblJugador.setFont(Styles.FUENTE_DLG_NEGRITA);
            lblJugador.setForeground(PaletaColor.oscuro(jugador.getColor()));
            lblJugador.setAlignmentX(Component.CENTER_ALIGNMENT);
            contenido.add(lblJugador);
            contenido.add(Box.createVerticalStrut(Styles.SEPARACION_BOTONES_DLG));
        }
        contenido.add(crearBoton("CAMBIAR COLORES", Styles.COLOR_BOTON_FONDO, this::cambiarColores));
        contenido.add(Box.createVerticalStrut(Styles.SEPARACION_BOTONES_DLG));
        contenido.add(crearBoton("ABANDONAR PARTIDA", Styles.COLOR_BOTON_NEUTRO, this::abandonarPartida));
        contenido.add(Box.createVerticalStrut(Styles.SEPARACION_BOTONES_DLG));
        contenido.add(crearBoton("TERMINAR PARTIDA", Styles.COLOR_BOTON_PELIGRO, this::terminarPartida));

        setLayout(new BorderLayout());
        add(contenido, BorderLayout.CENTER);
        pack();
        setSize(new Dimension(Styles.ANCHO_DLG_OPCIONES, getHeight()));
        setResizable(false);
        setLocationRelativeTo(getOwner());
    }

    private BtnRedondeado crearBoton(String texto, Color color, Runnable accion) {
        BtnRedondeado boton = new BtnRedondeado(texto, color, Styles.COLOR_BOTON_TEXTO);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, boton.getPreferredSize().height));
        boton.addActionListener(e -> accion.run());
        return boton;
    }

    private void cambiarColores() {
        dispose();
        new DlgColores(propietario, vistaJugadores.getJugadores(), control::cambiarColores).setVisible(true);
    }

    private void abandonarPartida() {
        Dialogos.preguntar(this,
                "¿Abandonar la partida?\nTus cuadros desaparecerán y se cerrará esta ventana.",
                "Abandonar partida", JOptionPane.WARNING_MESSAGE, acepta -> {
                    if (acepta) {
                        dispose();
                        control.abandonar();
                    }
                });
    }

    private void terminarPartida() {
        Dialogos.preguntar(this,
                "¿Proponer terminar la partida?\nTodos los demás jugadores deben estar de acuerdo.",
                "Terminar partida", JOptionPane.QUESTION_MESSAGE, acepta -> {
                    if (acepta) {
                        dispose();
                        control.proponerFin();
                    }
                });
    }
}
