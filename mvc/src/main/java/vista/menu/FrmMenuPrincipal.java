package vista.menu;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import controlador.IControlMenu;
import modelo.ModeloMenu;
import modelo.OpcionPartida;
import vista.componentes.BtnRedondeado;
import vista.util.Styles;

public class FrmMenuPrincipal extends JFrame {

    private final ModeloMenu modelo;
    private IControlMenu control;

    private final JLabel lblJugadores = new JLabel();
    private int jugadoresSeleccionados;

    public FrmMenuPrincipal(ModeloMenu modelo) {
        super(Styles.TITULO_VENTANA);
        this.modelo = modelo;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        construir();
    }

    public void setControl(IControlMenu control) {
        this.control = control;
    }

    private void construir() {
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Styles.COLOR_FONDO);
        int m = Styles.MARGEN_MENU_PRINCIPAL;
        contenido.setBorder(BorderFactory.createEmptyBorder(m, m, m, m));

        JLabel lblTitulo = new JLabel("TIMBIRICHE");
        lblTitulo.setFont(Styles.FUENTE_TITULO_MENU);
        lblTitulo.setForeground(Styles.COLOR_BOTON_FONDO);
        contenido.add(centrado(lblTitulo));
        contenido.add(Box.createVerticalStrut(6));

        JLabel lblSubtitulo = new JLabel("Selecciona el número de jugadores");
        lblSubtitulo.setFont(Styles.FUENTE_SUBTITULO_MENU);
        lblSubtitulo.setForeground(Styles.COLOR_TEXTO_SECUNDARIO);
        contenido.add(centrado(lblSubtitulo));
        contenido.add(Box.createVerticalStrut(20));

        JPanel pnlOpciones = new JPanel();
        pnlOpciones.setLayout(new BoxLayout(pnlOpciones, BoxLayout.Y_AXIS));
        pnlOpciones.setOpaque(false);
        ButtonGroup grupo = new ButtonGroup();
        boolean primera = true;
        for (OpcionPartida opcion : modelo.getOpciones()) {
            int p = opcion.puntosPorLado();
            JRadioButton rb = new JRadioButton(opcion.jugadores() + " jugadores  —  tablero de " + p + " × " + p + " puntos");
            rb.setFont(Styles.FUENTE_OPCION_MENU);
            rb.setOpaque(false);
            rb.setFocusPainted(false);
            rb.addActionListener(e -> seleccionar(opcion.jugadores()));
            grupo.add(rb);
            pnlOpciones.add(rb);
            pnlOpciones.add(Box.createVerticalStrut(6));
            if (primera) {
                rb.setSelected(true);
                seleccionar(opcion.jugadores());
                primera = false;
            }
        }
        contenido.add(centrado(pnlOpciones));
        contenido.add(Box.createVerticalStrut(14));

        lblJugadores.setFont(Styles.FUENTE_DLG);
        contenido.add(centrado(lblJugadores));
        contenido.add(Box.createVerticalStrut(4));
        JLabel lblNota = new JLabel("(nombres y avatares provisionales; todos juegan en esta ventana)");
        lblNota.setFont(Styles.FUENTE_NOTA_MENU);
        lblNota.setForeground(Styles.COLOR_TEXTO_SECUNDARIO);
        contenido.add(centrado(lblNota));
        contenido.add(Box.createVerticalStrut(26));

        BtnRedondeado btnIniciar = new BtnRedondeado("INICIAR PARTIDA", Styles.COLOR_BOTON_FONDO, Styles.COLOR_BOTON_TEXTO);
        BtnRedondeado btnSalir = new BtnRedondeado("SALIR", Styles.COLOR_BOTON_NEUTRO, Styles.COLOR_BOTON_TEXTO);
        btnIniciar.addActionListener(e -> {
            if (control != null) {
                control.iniciarPartida(jugadoresSeleccionados);
            }
        });
        btnSalir.addActionListener(e -> {
            if (control != null) {
                control.salir();
            }
        });
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        botones.setOpaque(false);
        botones.add(btnSalir);
        botones.add(btnIniciar);
        contenido.add(centrado(botones));

        setLayout(new BorderLayout());
        add(contenido, BorderLayout.CENTER);
        pack();
        setResizable(false);
    }

    private void seleccionar(int jugadores) {
        jugadoresSeleccionados = jugadores;
        lblJugadores.setText("Jugadores: " + String.join(", ", modelo.getNombresProvisionales(jugadores)));
    }

    private static Component centrado(JPanel panel) {
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(panel.getPreferredSize());
        return panel;
    }

    private static Component centrado(JLabel label) {
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    public void mostrar() {
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void ocultar() {
        setVisible(false);
    }
}
