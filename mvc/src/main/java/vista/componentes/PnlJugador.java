package vista.componentes;

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
import modelo.DatosJugador;
import modelo.Ubicacion;
import vista.util.Styles;

public class PnlJugador extends JPanel {

    private final DatosJugador datos;
    private final Ubicacion ubicacion;

    private final JLabel lblNombre = new JLabel();
    private final JLabel lblTextoPuntos = new JLabel(Styles.TEXTO_PUNTOS);
    private final JLabel lblValorPuntos = new JLabel("0");
    private final PnlAvatar pnlAvatar = new PnlAvatar();

    private boolean enTurno = false;

    public PnlJugador(DatosJugador datos, Ubicacion ubicacion) {
        this.datos = datos;
        this.ubicacion = ubicacion;
        setOpaque(false);
        setLayout(new BorderLayout());
        configurarEtiquetas();
        armarSegunUbicacion();
        aplicarPreferredSize();
        aplicarColores();
    }

    private void configurarEtiquetas() {
        lblNombre.setText(datos.nombre());
        lblNombre.setFont(Styles.FUENTE_NOMBRE);
        lblNombre.setHorizontalAlignment(SwingConstants.CENTER);

        lblTextoPuntos.setFont(Styles.FUENTE_PUNTOS_ETIQUETA);
        lblValorPuntos.setFont(Styles.FUENTE_PUNTOS_VALOR);
    }

    private void armarSegunUbicacion() {
        int alineacionPuntos = (ubicacion == Ubicacion.ESTE) ? FlowLayout.RIGHT : FlowLayout.LEFT;

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

        switch (ubicacion) {
            case NORTE -> {
                add(pnlPuntos, BorderLayout.NORTH);
                add(contenedorAvatar, BorderLayout.CENTER);
                add(pnlNombre, BorderLayout.SOUTH);
            }
            case SUR -> {
                add(pnlNombre, BorderLayout.NORTH);
                add(contenedorAvatar, BorderLayout.CENTER);
                add(pnlPuntos, BorderLayout.SOUTH);
            }
            case OESTE, ESTE -> {
                add(pnlPuntos, BorderLayout.NORTH);
                add(contenedorAvatar, BorderLayout.CENTER);
                add(pnlNombre, BorderLayout.SOUTH);
            }
        }
    }

    private void aplicarPreferredSize() {
        int grosor = Styles.GROSOR_PANEL_JUGADOR;
        Dimension d = switch (ubicacion) {
            case NORTE, SUR -> new Dimension(grosor * 2, grosor);
            case ESTE, OESTE -> new Dimension(grosor, grosor * 2);
        };
        setPreferredSize(d);
    }

    public DatosJugador getDatos() {
        return datos;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setEnTurno(boolean enTurno) {
        this.enTurno = enTurno;
        aplicarColores();
        repaint();
    }

    public void setPuntos(int puntos) {
        lblValorPuntos.setText(String.valueOf(puntos));
    }

    private void aplicarColores() {
        var p = datos.paleta();
        lblNombre.setForeground(p.getOscuro());
        lblTextoPuntos.setForeground(p.getOscuro());
        lblValorPuntos.setForeground(p.getOscuro());
        pnlAvatar.setColores(p.getClaro(), p.getFuerte(), p.getFuerte());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(enTurno ? datos.paleta().getFuerte() : datos.paleta().getPastel());

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