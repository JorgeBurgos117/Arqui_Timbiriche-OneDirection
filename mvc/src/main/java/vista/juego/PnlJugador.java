package vista.juego;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import presentacion.dtos.JugadorDTO;
import vista.componentes.PnlAvatar;
import vista.util.Styles;
import vista.util.Tonos;
import vista.util.Ubicacion;

/**
 * Panel de un jugador en su lado del tablero.
 * Dibuja un medio círculo de color y, DENTRO de él: avatar, nombre y abajo del nombre los puntos.
 */
public class PnlJugador extends JPanel {

    private static final int SEPARACION_BORDE = 8;

    private final Ubicacion ubicacion;

    private final JLabel lblNombre = new JLabel(" ");
    private final JLabel lblPuntos = new JLabel(" ");
    private final PnlAvatar pnlAvatar = new PnlAvatar();

    private JugadorDTO datos;

    public PnlJugador(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
        setOpaque(false);
        setLayout(new GridBagLayout());
        armar();
        aplicarPreferredSize();
    }

    private void armar() {
        lblNombre.setFont(Styles.FUENTE_NOMBRE);
        lblPuntos.setFont(Styles.FUENTE_PUNTOS_ETIQUETA.deriveFont(13f));
        pnlAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPuntos.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Columna: avatar, nombre y debajo los puntos
        JPanel columna = new JPanel();
        columna.setOpaque(false);
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        if (ubicacion == Ubicacion.SUR) {          // abajo: el avatar queda pegado al borde inferior
            columna.add(lblNombre);
            columna.add(lblPuntos);
            columna.add(Box.createVerticalStrut(4));
            columna.add(pnlAvatar);
        } else {
            columna.add(pnlAvatar);
            columna.add(Box.createVerticalStrut(4));
            columna.add(lblNombre);
            columna.add(lblPuntos);
        }

        // La columna se pega al lado de la ventana, para que quede dentro del medio círculo
        GridBagConstraints c = new GridBagConstraints();
        int s = SEPARACION_BORDE;
        switch (ubicacion) {
            case NORTE -> { c.anchor = GridBagConstraints.NORTH; c.insets = new Insets(s, 0, 0, 0); }
            case SUR -> { c.anchor = GridBagConstraints.SOUTH; c.insets = new Insets(0, 0, s, 0); }
            case OESTE -> { c.anchor = GridBagConstraints.WEST; c.insets = new Insets(0, s, 0, 0); }
            case ESTE -> { c.anchor = GridBagConstraints.EAST; c.insets = new Insets(0, 0, 0, s); }
        }
        c.weightx = 1;
        c.weighty = 1;
        add(columna, c);
    }

    private void aplicarPreferredSize() {
        int grosor = Styles.GROSOR_PANEL_JUGADOR;
        setPreferredSize(switch (ubicacion) {
            case NORTE, SUR -> new Dimension(grosor * 2, grosor);
            case ESTE, OESTE -> new Dimension(grosor, grosor * 2);
        });
    }

    public void actualizar(JugadorDTO jugador) {
        this.datos = jugador;
        Color fuerte = jugador.getColor();
        // Sobre el color fuerte (turno) el texto va en blanco; sobre el pastel, en el tono oscuro
        Color texto = jugador.isEnTurno() ? Styles.COLOR_BOTON_TEXTO : Tonos.oscuro(fuerte);
        lblNombre.setText(jugador.getNombre());
        lblNombre.setForeground(texto);
        lblPuntos.setText(Styles.TEXTO_PUNTOS + " " + jugador.getPuntaje());
        lblPuntos.setForeground(texto);
        pnlAvatar.setColores(Tonos.claro(fuerte), fuerte, fuerte);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (datos == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // En turno: medio círculo de color fuerte; si no: pastel
        g2.setColor(datos.isEnTurno() ? datos.getColor() : Tonos.pastel(datos.getColor()));
        int w = getWidth();
        int h = getHeight();
        int r = Styles.GROSOR_PANEL_JUGADOR;
        switch (ubicacion) {
            case NORTE -> g2.fillArc(w / 2 - r, -r, r * 2, r * 2, 180, 180);
            case SUR -> g2.fillArc(w / 2 - r, h - r, r * 2, r * 2, 0, 180);
            case OESTE -> g2.fillArc(-r, h / 2 - r, r * 2, r * 2, -90, 180);
            case ESTE -> g2.fillArc(w - r, h / 2 - r, r * 2, r * 2, 90, 180);
        }
        g2.dispose();
    }
}