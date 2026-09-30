package vista.juego;

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
import controlador.IControlVista;
import modelo.IModeloVista;
import modelo.JugadorVisual;
import vista.componentes.BtnRedondeado;
import vista.util.Styles;

public class DlgOpciones extends JDialog {

    private final Window propietario;
    private final IModeloVista modelo;
    private final IControlVista control;
    private final JugadorVisual actor;

    public DlgOpciones(Window propietario, IModeloVista modelo, IControlVista control) {
        super(propietario, Styles.TITULO_DLG_OPCIONES, ModalityType.APPLICATION_MODAL);
        this.propietario = propietario;
        this.modelo = modelo;
        this.control = control;
        this.actor = modelo.getJugadorEnTurno();
        construir();
    }

    private void construir() {
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Styles.COLOR_FONDO);
        int m = Styles.MARGEN_DLG;
        contenido.setBorder(BorderFactory.createEmptyBorder(m, m, m, m));

        if (actor != null) {
            JLabel lblActor = new JLabel("En turno: " + actor.nombre());
            lblActor.setFont(Styles.FUENTE_DLG_NEGRITA);
            lblActor.setForeground(actor.paleta().getOscuro());
            lblActor.setAlignmentX(Component.CENTER_ALIGNMENT);
            contenido.add(lblActor);
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
        new DlgColores(propietario, modelo.getJugadores(), modelo.getPaletasDisponibles(),
                control::cambiarColores).setVisible(true);
    }

    private void abandonarPartida() {
        if (actor == null) {
            return;
        }
        int r = JOptionPane.showConfirmDialog(this,
                "¿" + actor.nombre() + " abandona la partida?\nSus cuadros desaparecerán del tablero.",
                "Abandonar partida", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            dispose();
            control.abandonar();
        }
    }

    private void terminarPartida() {
        if (actor == null) {
            return;
        }
        int r = JOptionPane.showConfirmDialog(this,
                "¿" + actor.nombre() + " propone terminar la partida?\nTodos los demás deben estar de acuerdo.",
                "Terminar partida", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            dispose();
            control.proponerFin();
        }
    }
}
