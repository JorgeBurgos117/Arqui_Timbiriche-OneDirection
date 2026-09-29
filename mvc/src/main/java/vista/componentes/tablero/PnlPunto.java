package vista.componentes.tablero;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import modelo.Coordenada;
import modelo.PaletaColor;
import vista.util.Styles;

public class PnlPunto extends JPanel {

    public enum Estado {
        NORMAL, CANDIDATO, SELECCIONADO
    }

    public interface EscuchaPunto {

        void puntoEntro(Coordenada coordenada);

        void puntoSalio(Coordenada coordenada);

        void puntoPresionado(Coordenada coordenada);
    }

    private final Coordenada coordenada;
    private Estado estado = Estado.NORMAL;
    private boolean hover = false;
    private PaletaColor paletaTurno;

    public PnlPunto(Coordenada coordenada, EscuchaPunto escucha) {
        this.coordenada = coordenada;
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
                if (escucha != null) {
                    escucha.puntoEntro(coordenada);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
                if (escucha != null) {
                    escucha.puntoSalio(coordenada);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (escucha != null) {
                    escucha.puntoPresionado(coordenada);
                }
            }
        });
    }

    public Coordenada getCoordenada() {
        return coordenada;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
        repaint();
    }

    public Estado getEstado() {
        return estado;
    }

    public void setPaletaTurno(PaletaColor paletaTurno) {
        this.paletaTurno = paletaTurno;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color color = Styles.COLOR_PUNTO_NEUTRO;
        int diametro = Styles.DIAMETRO_PUNTO;

        if (paletaTurno != null) {
            switch (estado) {
                case SELECCIONADO -> {
                    color = paletaTurno.getFuerte();
                    diametro = Styles.DIAMETRO_PUNTO_RESALTADO;
                }
                case CANDIDATO -> {
                    color = hover ? paletaTurno.getFuerte() : paletaTurno.getPastel();
                    diametro = Styles.DIAMETRO_PUNTO_RESALTADO;
                }
                case NORMAL -> {
                    if (hover) {
                        color = paletaTurno.getPastel();
                        diametro = Styles.DIAMETRO_PUNTO_RESALTADO;
                    }
                }
            }
        }

        int x = (getWidth() - diametro) / 2;
        int y = (getHeight() - diametro) / 2;
        g2.setColor(color);
        g2.fillOval(x, y, diametro, diametro);
        g2.dispose();
    }
}