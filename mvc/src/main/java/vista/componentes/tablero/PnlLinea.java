package vista.componentes.tablero;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import modelo.Arista;
import modelo.PaletaColor;
import vista.util.Styles;


public class PnlLinea extends JPanel {

    public enum Estado {
        OCULTA, PREVIEW, TRAZADA
    }

    private final Arista arista;
    private Estado estado = Estado.OCULTA;
    private PaletaColor paleta;

    public PnlLinea(Arista arista) {
        this.arista = arista;
        setOpaque(false);
    }

    public Arista getArista() {
        return arista;
    }

    public Estado getEstado() {
        return estado;
    }

    public void mostrarPreview(PaletaColor paleta) {
        if (estado == Estado.TRAZADA) {
            return;
        }
        this.paleta = paleta;
        this.estado = Estado.PREVIEW;
        repaint();
    }

    public void ocultar() {
        if (estado == Estado.TRAZADA) {
            return;
        }
        this.estado = Estado.OCULTA;
        repaint();
    }

    public void trazar(PaletaColor paleta) {
        this.paleta = paleta;
        this.estado = Estado.TRAZADA;
        repaint();
    }

    public void reiniciar() {
        this.estado = Estado.OCULTA;
        this.paleta = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (estado == Estado.OCULTA || paleta == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color color = (estado == Estado.PREVIEW) ? paleta.getPastel() : paleta.getFuerte();
        g2.setColor(color);
        g2.setStroke(new BasicStroke(Styles.GROSOR_LINEA, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        if (arista.esHorizontal()) {
            int y = getHeight() / 2;
            g2.drawLine(0, y, getWidth(), y);
        } else {
            int x = getWidth() / 2;
            g2.drawLine(x, 0, x, getHeight());
        }
        g2.dispose();
    }
}