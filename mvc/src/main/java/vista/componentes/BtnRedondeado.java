package vista.componentes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import modelo.PaletaColor;
import vista.util.Styles;

/**
 * Boton plano con esquinas redondeadas, para no depender del LookAndFeel.
 */
public class BtnRedondeado extends JButton {

    private final Color colorFondo;
    private final Color colorHover;
    private boolean hover = false;

    public BtnRedondeado(String texto, Color fondo, Color textoColor) {
        super(texto);
        this.colorFondo = fondo;
        this.colorHover = PaletaColor.mezclar(fondo, Color.BLACK, 0.15f);

        setFont(Styles.FUENTE_BOTON);
        setForeground(textoColor);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(hover ? colorHover : colorFondo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), Styles.RADIO_BOTON, Styles.RADIO_BOTON);
        g2.dispose();
        super.paintComponent(g);
    }
}