package vista.juego;

import controlador.ControlEjercerTurno;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import presentacion.dtos.FiguraDTO;
import presentacion.dtos.TipoFigura;
import presentacion.modelo.EventoPresentacion;
import presentacion.modelo.IConsultaModeloPresentacion;
import presentacion.modelo.IObservadorPresentacion;
import vista.util.Styles;
import vista.util.Tonos;

/**
 * Dibuja el tablero. No sabe nada del juego: solo recibe FiguraDTO
 * (tipo + coordenadas en pixeles + color) y los pinta.
 */
public class VistaTablero extends JPanel implements IObservadorPresentacion {

    private final IConsultaModeloPresentacion modelo;
    private final int tamanoCelda;   // distancia entre puntos, para que grosores y tamaños sean proporcionales
    private ControlEjercerTurno controlador;

    private final Map<String, FiguraDTO> figuras = new LinkedHashMap<>();

    public VistaTablero(IConsultaModeloPresentacion modelo, int lado, int tamanoCelda) {
        this.modelo = modelo;
        this.tamanoCelda = tamanoCelda;
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Dimension d = new Dimension(lado, lado);
        setPreferredSize(d);
        setMinimumSize(d);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (controlador != null) {
                    controlador.seleccionarPunto(e.getX(), e.getY());
                }
            }
        });
    }

    public void setControlador(ControlEjercerTurno controlador) {
        this.controlador = controlador;
    }

    // ===================== avisos del Modelo de Presentación =====================

    @Override
    public void actualizar(EventoPresentacion evento) {
        switch (evento) {
            case FIGURAS_ACTUALIZADAS -> {
                for (FiguraDTO figura : modelo.getFiguras()) {   // saca lo que hay en la cola
                    figuras.put(figura.getClave(), figura);
                }
                repaint();
            }
            case ADVERTENCIA -> JOptionPane.showMessageDialog(this, modelo.getMensajeAdvertencia(),
                    Styles.TITULO_VENTANA, JOptionPane.WARNING_MESSAGE);
            default -> { }   // jugadores y fin de partida los atiende VistaJugadores
        }
    }

    // ===================== dibujo =====================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Styles.COLOR_FONDO_TABLERO);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), Styles.ARCO_FONDO_TABLERO, Styles.ARCO_FONDO_TABLERO));

        // Orden: cuadros abajo, luego líneas, y los puntos hasta arriba
        for (TipoFigura tipo : new TipoFigura[]{TipoFigura.CUADRO, TipoFigura.LINEA, TipoFigura.PUNTO}) {
            for (FiguraDTO f : figuras.values()) {
                if (f.getTipo() == tipo) {
                    dibujar(g2, f);
                }
            }
        }
        g2.dispose();
    }

    private void dibujar(Graphics2D g2, FiguraDTO f) {
        switch (f.getTipo()) {
            case PUNTO -> {
                boolean neutro = esNeutro(f.getColor());
                double d = neutro ? medida(0.14, 4, 8) : medida(0.32, 8, 16);
                g2.setColor(neutro ? Styles.COLOR_PUNTO_NEUTRO : f.getColor());
                g2.fill(new Ellipse2D.Double(f.getX1() - d / 2, f.getY1() - d / 2, d, d));
            }
            case LINEA -> {
                g2.setColor(f.getColor());
                g2.setStroke(new BasicStroke((float) medida(0.16, 3, 8), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Line2D.Double(f.getX1(), f.getY1(), f.getX2(), f.getY2()));
            }
            case CUADRO -> {
                double inset = medida(0.12, 2, 6);
                double arco = medida(0.18, 3, 8);
                g2.setColor(Tonos.pastel(f.getColor()));
                g2.fill(new RoundRectangle2D.Double(f.getX1() + inset, f.getY1() + inset,
                        f.getX2() - f.getX1() - 2 * inset, f.getY2() - f.getY1() - 2 * inset, arco, arco));
            }
        }
    }

    /** Tamaño proporcional a la celda, con un mínimo y un máximo (igual que la versión anterior). */
    private double medida(double factor, double min, double max) {
        return Math.max(min, Math.min(max, tamanoCelda * factor));
    }

    /** Los puntos sin resaltar llegan en gris. */
    private boolean esNeutro(Color c) {
        return Color.GRAY.equals(c);
    }
}