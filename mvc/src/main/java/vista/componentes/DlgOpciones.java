package vista.componentes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import controlador.IControlJuego;
import vista.util.Styles;
import vista.componentes.BtnRedondeado;

//Falta agregar lógica a todos los botones

public class DlgOpciones extends JDialog {

    public static final String OPCION_CAMBIAR_COLORES = "cambiarColores";
    public static final String OPCION_ABANDONAR_PARTIDA = "abandonarPartida";
    public static final String OPCION_TERMINAR_PARTIDA = "terminarPartida";

    private final IControlJuego control;

    public DlgOpciones(Window propietario, IControlJuego control) {
        super(propietario, Styles.TITULO_DLG_OPCIONES, ModalityType.APPLICATION_MODAL);
        this.control = control;
        construir();
    }

    private void construir() {
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Styles.COLOR_FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(
                Styles.MARGEN_DLG_OPCIONES, Styles.MARGEN_DLG_OPCIONES,
                Styles.MARGEN_DLG_OPCIONES, Styles.MARGEN_DLG_OPCIONES));

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

    private BtnRedondeado crearBoton(String texto, java.awt.Color color, Runnable accion) {
        BtnRedondeado boton = new BtnRedondeado(texto, color, Styles.COLOR_BOTON_TEXTO);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, boton.getPreferredSize().height));
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                accion.run();
            }
        });
        return boton;
    }

    // ------------------------------------------------- acciones (aun sin logica)
    private void cambiarColores() {
        System.out.println(OPCION_CAMBIAR_COLORES);
        notificar(OPCION_CAMBIAR_COLORES);
    }

    private void abandonarPartida() {
        System.out.println(OPCION_ABANDONAR_PARTIDA);
        notificar(OPCION_ABANDONAR_PARTIDA);
    }

    private void terminarPartida() {
        System.out.println(OPCION_TERMINAR_PARTIDA);
        notificar(OPCION_TERMINAR_PARTIDA);
    }

    private void notificar(String opcion) {
        if (control != null) {
            control.opcionSeleccionada(opcion);
        }
    }
}