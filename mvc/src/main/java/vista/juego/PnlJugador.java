package vista.juego;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import modelo.JugadorVisual;
import modelo.PaletaColor;
import modelo.Ubicacion;
import vista.componentes.PnlAvatar;
import vista.util.Styles;

public class PnlJugador extends JPanel {

    private final Ubicacion ubicacion;

    private final JLabel lblNombre = new JLabel(" ");
    private final JLabel lblTextoPuntos = new JLabel(Styles.TEXTO_PUNTOS);
    private final JLabel lblValorPuntos = new JLabel("0");
    private final PnlAvatar pnlAvatar = new PnlAvatar();

    private JugadorVisual datos;

    public PnlJugador(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
        setOpaque(false);
        setLayout(new BorderLayout());
        configurarEtiquetas();
        armarSegunUbicacion();
        aplicarPreferredSize();
    }

    private void configurarEtiquetas() {
        lblNombre.setFont(Styles.FUENTE_NOMBRE);
        lblNombre.setHorizontalAlignment(SwingConstants.CENTER);
        lblTextoPuntos.setFont(Styles.FUENTE_PUNTOS_ETIQUETA);
        lblValorPuntos.setFont(Styles.FUENTE_PUNTOS_VALOR);
    }

    private void armarSegunUbicacion() {
        int alineacionPuntos = (ubicacion == Ubicacion.ESTE || ubicacion == Ubicacion.NORTE)
                ? FlowLayout.RIGHT
                : FlowLayout.LEFT;

        JPanel pnlPuntos = new JPanel(new FlowLayout(alineacionPuntos, 8, 4));
        pnlPuntos.setOpaque(false);
        pnlPuntos.add(lblTextoPuntos);
        pnlPuntos.add(lblValorPuntos);

        JPanel contenedorAvatar = new JPanel(new GridBagLayout());
        contenedorAvatar.setOpaque(false);
        contenedorAvatar.add(pnlAvatar);

        JPanel pnlNombre = new JPanel(new BorderLayout());
        pnlNombre.setOpaque(false);
        pnlNombre.setBorder(BorderFactory.createEmptyBorder(0, 8, 6, 8));
        pnlNombre.add(lblNombre, BorderLayout.CENTER);

        if (ubicacion == Ubicacion.SUR) {
            add(pnlNombre, BorderLayout.NORTH);
            add(contenedorAvatar, BorderLayout.CENTER);
            add(pnlPuntos, BorderLayout.SOUTH);
        } else {
            add(pnlPuntos, BorderLayout.NORTH);
            add(contenedorAvatar, BorderLayout.CENTER);
            add(pnlNombre, BorderLayout.SOUTH);
        }
    }

    private void aplicarPreferredSize() {
        int grosor = Styles.GROSOR_PANEL_JUGADOR;
        setPreferredSize(switch (ubicacion) {
            case NORTE, SUR -> new Dimension(grosor * 2, grosor);
            case ESTE, OESTE -> new Dimension(grosor, grosor * 2);
        });
    }

    public void actualizar(JugadorVisual jugador) {
        this.datos = jugador;
        PaletaColor p = jugador.paleta();
        lblNombre.setText(jugador.nombre());
        lblValorPuntos.setText(String.valueOf(jugador.puntos()));
        boolean sobreColorFuerte = jugador.enTurno() && (ubicacion == Ubicacion.NORTE || ubicacion == Ubicacion.SUR);
        lblNombre.setForeground(sobreColorFuerte ? Styles.COLOR_BOTON_TEXTO : p.getOscuro());
        lblTextoPuntos.setForeground(p.getOscuro());
        lblValorPuntos.setForeground(p.getOscuro());
        pnlAvatar.setColores(p.getClaro(), p.getFuerte(), p.getFuerte());
        repaint();
    }

    public void marcarAusente() {
        if (datos == null) {
            return;
        }
        datos = null;
        removeAll();
        revalidate();
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

        g2.setColor(datos.enTurno() ? datos.paleta().getFuerte() : datos.paleta().getPastel());
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
