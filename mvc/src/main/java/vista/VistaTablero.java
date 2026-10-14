package vista;

import controlador.ControlEjercerTurno;
import dtos.FiguraDTO;
import dtos.TipoFigura;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
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
import javax.swing.JPanel;
import javax.swing.Timer;
import modeloPresentacion.EventoPresentacion;
import modeloPresentacion.IConsultaModeloPresentacion;
import modeloPresentacion.ObservadorPresentacion;
import vista.util.PaletaColor;
import vista.util.Styles;

public class VistaTablero extends JPanel implements ObservadorPresentacion {

    private ControlEjercerTurno controlador;
    private IConsultaModeloPresentacion modelo;
    private final Map<String, FiguraDTO> figuras = new LinkedHashMap<>();
    private FiguraDTO puntoHover;
    private String advertencia;
    private final Timer timerAdvertencia;

    public VistaTablero() {
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        timerAdvertencia = new Timer(Styles.DURACION_ADVERTENCIA_MS, e -> {
            advertencia = null;
            repaint();
        });
        timerAdvertencia.setRepeats(false);

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                clicEnPunto(e.getX(), e.getY());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                actualizarHover(buscarPunto(e.getX(), e.getY()));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                actualizarHover(null);
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    public void setControlador(ControlEjercerTurno controlador) {
        this.controlador = controlador;
    }

    public void setModelo(IConsultaModeloPresentacion modelo) {
        this.modelo = modelo;
        int lado = modelo.getLadoTablero();
        Dimension d = new Dimension(lado, lado);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
    }

    @Override
    public void actualizar(EventoPresentacion evento) {
        switch (evento) {
            case FIGURAS_ACTUALIZADAS -> {
                for (FiguraDTO figura : modelo.getFiguras()) {
                    dibujar(figura);
                }
                repaint();
            }
            case ADVERTENCIA -> mostrarAdvertencia(modelo.getMensajeAdvertencia());
            case JUGADORES_ACTUALIZADOS, PARTIDA_TERMINADA -> repaint();
            default -> {
            }
        }
    }

    public void clicEnPunto(int x, int y) {
        if (controlador != null) {
            controlador.seleccionarPunto(x, y);
        }
    }

    public void dibujar(FiguraDTO figura) {
        String clave = clave(figura);
        if (figura.getTipo() == TipoFigura.CUADRO && figura.getColor() == null) {
            figuras.remove(clave);
        } else {
            figuras.put(clave, figura);
        }
        if (puntoHover != null && clave.equals(clave(puntoHover))) {
            puntoHover = figura;
        }
    }

    private static String clave(FiguraDTO f) {
        return switch (f.getTipo()) {
            case PUNTO -> "P" + f.getX1() + "," + f.getY1();
            case CUADRO -> "C" + f.getX1() + "," + f.getY1();
            case LINEA -> "L" + f.getX1() + "," + f.getY1() + "," + f.getX2() + "," + f.getY2();
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (modelo == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        pintarTablero(g2);
        pintarSeleccion(g2);
        pintarAdvertencia(g2);
        g2.dispose();
    }

    private void pintarTablero(Graphics2D g2) {
        double celda = modelo.getTamanoCelda();
        g2.setColor(Styles.COLOR_FONDO_TABLERO);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(),
                Styles.ARCO_FONDO_TABLERO, Styles.ARCO_FONDO_TABLERO));

        double inset = limitar(celda * 0.12, 2, 6);
        double arco = limitar(celda * 0.18, 3, 8);
        g2.setFont(Styles.FUENTE_INICIAL_CUADRO.deriveFont((float) limitar(celda * 0.38, 8, 20)));
        FontMetrics fm = g2.getFontMetrics();
        for (FiguraDTO f : figuras.values()) {
            if (f.getTipo() != TipoFigura.CUADRO) {
                continue;
            }
            double x = f.getX1() + inset;
            double y = f.getY1() + inset;
            double ancho = f.getX2() - f.getX1() - inset * 2;
            double alto = f.getY2() - f.getY1() - inset * 2;
            g2.setColor(PaletaColor.pastel(f.getColor()));
            g2.fill(new RoundRectangle2D.Double(x, y, ancho, alto, arco, arco));
            if (f.getEtiqueta() != null) {
                g2.setColor(PaletaColor.oscuro(f.getColor()));
                g2.drawString(f.getEtiqueta(),
                        (float) (x + (ancho - fm.stringWidth(f.getEtiqueta())) / 2),
                        (float) (y + (alto - fm.getHeight()) / 2 + fm.getAscent()));
            }
        }

        g2.setStroke(new BasicStroke((float) grosorLinea(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (FiguraDTO f : figuras.values()) {
            if (f.getTipo() == TipoFigura.LINEA) {
                g2.setColor(f.getColor());
                g2.draw(new Line2D.Double(f.getX1(), f.getY1(), f.getX2(), f.getY2()));
            }
        }
    }

    private void pintarSeleccion(Graphics2D g2) {
        boolean miTurno = modelo.esMiTurno();
        if (miTurno && puntoHover != null && esCandidato(puntoHover)) {
            g2.setColor(PaletaColor.pastel(puntoHover.getColor()));
            g2.setStroke(new BasicStroke((float) grosorLinea(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Line2D.Double(puntoHover.getX2(), puntoHover.getY2(), puntoHover.getX1(), puntoHover.getY1()));
        }

        double celda = modelo.getTamanoCelda();
        double chico = limitar(celda * 0.14, 4, 8);
        double grande = limitar(celda * 0.32, 8, 16);
        for (FiguraDTO f : figuras.values()) {
            if (f.getTipo() != TipoFigura.PUNTO) {
                continue;
            }
            boolean hover = miTurno && f == puntoHover;
            Color color;
            double diametro;
            if (f.getColor() == null) {
                color = hover ? Styles.COLOR_PUNTO_HOVER : Styles.COLOR_PUNTO_NEUTRO;
                diametro = hover ? grande * 0.8 : chico;
            } else if (esCandidato(f)) {
                color = hover ? f.getColor() : PaletaColor.pastel(f.getColor());
                diametro = grande;
            } else {
                color = f.getColor();
                diametro = grande;
            }
            g2.setColor(color);
            g2.fill(new Ellipse2D.Double(f.getX1() - diametro / 2, f.getY1() - diametro / 2, diametro, diametro));
        }
    }

    private void pintarAdvertencia(Graphics2D g2) {
        if (advertencia == null || advertencia.isBlank()) {
            return;
        }
        g2.setFont(Styles.FUENTE_ADVERTENCIA);
        FontMetrics fm = g2.getFontMetrics();
        int ancho = Math.min(getWidth() - 20, fm.stringWidth(advertencia) + 32);
        int alto = fm.getHeight() + 16;
        int x = (getWidth() - ancho) / 2;
        int y = getHeight() - alto - 14;
        g2.setColor(Styles.COLOR_ADVERTENCIA_FONDO);
        g2.fillRoundRect(x, y, ancho, alto, alto, alto);
        g2.setColor(Styles.COLOR_ADVERTENCIA_TEXTO);
        g2.drawString(advertencia, x + (ancho - fm.stringWidth(advertencia)) / 2, y + 8 + fm.getAscent());
    }

    private void mostrarAdvertencia(String mensaje) {
        advertencia = mensaje;
        timerAdvertencia.restart();
        repaint();
    }

    private void actualizarHover(FiguraDTO punto) {
        if (punto != puntoHover) {
            puntoHover = punto;
            repaint();
        }
    }

    private FiguraDTO buscarPunto(int x, int y) {
        if (modelo == null) {
            return null;
        }
        double radio = modelo.getTamanoCelda() * 0.45;
        for (FiguraDTO f : figuras.values()) {
            if (f.getTipo() == TipoFigura.PUNTO
                    && Math.abs(f.getX1() - x) <= radio && Math.abs(f.getY1() - y) <= radio) {
                return f;
            }
        }
        return null;
    }

    private static boolean esCandidato(FiguraDTO punto) {
        return punto.getColor() != null && (punto.getX1() != punto.getX2() || punto.getY1() != punto.getY2());
    }

    private double grosorLinea() {
        return limitar(modelo.getTamanoCelda() * 0.16, 3, 8);
    }

    private static double limitar(double valor, double min, double max) {
        return Math.max(min, Math.min(max, valor));
    }
}
