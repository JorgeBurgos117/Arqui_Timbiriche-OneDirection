package vista.componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import vista.util.Styles;

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
        dibujar(g2, (getWidth() - d) / 2, (getHeight() - d) / 2, d, colorFondo, colorSilueta, colorBorde);
        g2.dispose();
    }

    /**
     * Dibuja el avatar (círculo con silueta) de diámetro d en (x0, y0).
     * Es estático para que PnlJugador lo pueda dibujar del tamaño que necesite.
     */
    public static void dibujar(Graphics2D g, int x0, int y0, int d, Color fondo, Color silueta, Color borde) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(fondo);
        g2.fillOval(x0, y0, d, d);

        g2.clip(new java.awt.geom.Ellipse2D.Double(x0, y0, d, d));
        g2.setColor(silueta);
        int diamCabeza = Math.round(d * 0.30f);
        int xCabeza = x0 + (d - diamCabeza) / 2;
        int yCabeza = y0 + Math.round(d * 0.20f);
        g2.fillOval(xCabeza, yCabeza, diamCabeza, diamCabeza);

        int anchoCuerpo = Math.round(d * 0.60f);
        int altoCuerpo = Math.round(d * 0.70f);
        int xCuerpo = x0 + (d - anchoCuerpo) / 2;
        int yCuerpo = y0 + Math.round(d * 0.56f);
        g2.fillArc(xCuerpo, yCuerpo, anchoCuerpo, altoCuerpo, 0, 180);
        g2.dispose();

        // el grosor del borde también es proporcional (3 px cuando el avatar mide 80)
        float grosor = Math.max(1.5f, Styles.GROSOR_BORDE_AVATAR * d / (float) Styles.DIAMETRO_AVATAR);
        Graphics2D gb = (Graphics2D) g.create();
        gb.setColor(borde);
        gb.setStroke(new BasicStroke(grosor));
        gb.draw(new java.awt.geom.Ellipse2D.Double(x0 + grosor / 2, y0 + grosor / 2, d - grosor, d - grosor));
        gb.dispose();
    }
}