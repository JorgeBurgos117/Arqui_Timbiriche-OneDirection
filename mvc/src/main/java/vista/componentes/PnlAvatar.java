package vista.componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import vista.util.Styles;

/**
 * Avatar generico dibujado por codigo (circulo + silueta), tintado con la
 * paleta del jugador. Si despues quieres imagenes, aqui va el setIcon.
 */
public class PnlAvatar extends JPanel {

    private Color colorFondo = Color.LIGHT_GRAY;
    private Color colorSilueta = Color.DARK_GRAY;
    private Color colorBorde = Color.GRAY;

    public PnlAvatar() {
        setOpaque(false);
        int d = Styles.DIAMETRO_AVATAR;
        setPreferredSize(new Dimension(d, d));
        setMinimumSize(new Dimension(d, d));
        setMaximumSize(new Dimension(d, d));
    }

    public void setColores(Color fondo, Color silueta, Color borde) {
        this.colorFondo = fondo;
        this.colorSilueta = silueta;
        this.colorBorde = borde;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int d = Math.min(getWidth(), getHeight());
        int x0 = (getWidth() - d) / 2;
        int y0 = (getHeight() - d) / 2;

        // Circulo de fondo
        g2.setColor(colorFondo);
        g2.fillOval(x0, y0, d, d);

        // Silueta: cabeza + hombros
        g2.setColor(colorSilueta);
        int diamCabeza = Math.round(d * 0.30f);
        int xCabeza = x0 + (d - diamCabeza) / 2;
        int yCabeza = y0 + Math.round(d * 0.20f);
        g2.fillOval(xCabeza, yCabeza, diamCabeza, diamCabeza);

        int anchoCuerpo = Math.round(d * 0.60f);
        int altoCuerpo = Math.round(d * 0.70f);
        int xCuerpo = x0 + (d - anchoCuerpo) / 2;
        int yCuerpo = y0 + Math.round(d * 0.56f);
        g2.fillArc(xCuerpo, yCuerpo, anchoCuerpo, altoCuerpo, 0, 180);

        // Borde
        g2.setColor(colorBorde);
        g2.setStroke(new BasicStroke(Styles.GROSOR_BORDE_AVATAR));
        int off = Styles.GROSOR_BORDE_AVATAR / 2;
        g2.drawOval(x0 + off, y0 + off, d - Styles.GROSOR_BORDE_AVATAR, d - Styles.GROSOR_BORDE_AVATAR);

        g2.dispose();
    }
}