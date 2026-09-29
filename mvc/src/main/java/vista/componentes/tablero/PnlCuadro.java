package vista.componentes.tablero;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import modelo.Coordenada;
import modelo.PaletaColor;
import vista.util.Styles;

public class PnlCuadro extends JPanel {

    private final Coordenada esquina;
    private PaletaColor paleta;
    private char inicial = ' ';

    public PnlCuadro(Coordenada esquina) {
        this.esquina = esquina;
        setOpaque(false);
    }

    public Coordenada getEsquina() {
        return esquina;
    }

    public boolean estaPintado() {
        return paleta != null;
    }

    public void pintar(PaletaColor paleta, char inicial) {
        this.paleta = paleta;
        this.inicial = inicial;
        repaint();
    }

    public void reiniciar() {
        this.paleta = null;
        this.inicial = ' ';
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (paleta == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(paleta.getPastel());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), Styles.ARCO_CUADRO, Styles.ARCO_CUADRO);

        g2.setColor(paleta.getOscuro());
        g2.setFont(Styles.FUENTE_INICIAL_CUADRO);
        FontMetrics fm = g2.getFontMetrics();
        String texto = String.valueOf(inicial);
        int x = (getWidth() - fm.stringWidth(texto)) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(texto, x, y);

        g2.dispose();
    }
}