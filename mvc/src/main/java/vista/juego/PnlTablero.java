package vista.juego;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Set;
import javax.swing.JPanel;
import controlador.IControlVista;
import juego.dominio.Arista;
import juego.dominio.Coordenada;
import modelo.CuadroVisual;
import modelo.GeometriaTablero;
import modelo.IModeloVista;
import modelo.LineaVisual;
import modelo.PaletaColor;
import vista.util.Styles;

public class PnlTablero extends JPanel {

    private final IModeloVista modelo;
    private IControlVista control;

    public PnlTablero(IModeloVista modelo) {
        this.modelo = modelo;
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        int lado = modelo.getGeometria().getLado();
        Dimension d = new Dimension(lado, lado);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (control != null) {
                    control.clickTablero(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (control != null) {
                    control.moverCursor(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (control != null) {
                    control.salirTablero();
                }
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    public void setControl(IControlVista control) {
        this.control = control;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        GeometriaTablero geo = modelo.getGeometria();
        pintarFondo(g2, geo);
        pintarCuadros(g2, geo);
        pintarLineas(g2, geo);
        pintarPreview(g2, geo);
        pintarPuntos(g2, geo);

        g2.dispose();
    }

    private void pintarFondo(Graphics2D g2, GeometriaTablero geo) {
        g2.setColor(Styles.COLOR_FONDO_TABLERO);
        int arco = Styles.ARCO_FONDO_TABLERO;
        g2.fill(new RoundRectangle2D.Double(0, 0, geo.getLado(), geo.getLado(), arco, arco));
    }

    private void pintarCuadros(Graphics2D g2, GeometriaTablero geo) {
        double inset = geo.insetCuadro();
        double lado = geo.getEspaciado() - inset * 2;
        double arco = geo.arcoCuadro();
        Font fuente = Styles.FUENTE_INICIAL_CUADRO.deriveFont(geo.tamFuenteInicial());
        g2.setFont(fuente);
        FontMetrics fm = g2.getFontMetrics();

        for (CuadroVisual cuadro : modelo.getCuadros()) {
            double x = geo.x(cuadro.esquina().columna()) + inset;
            double y = geo.y(cuadro.esquina().fila()) + inset;
            g2.setColor(cuadro.paleta().getPastel());
            g2.fill(new RoundRectangle2D.Double(x, y, lado, lado, arco, arco));

            String texto = String.valueOf(cuadro.inicial());
            g2.setColor(cuadro.paleta().getOscuro());
            float tx = (float) (x + (lado - fm.stringWidth(texto)) / 2);
            float ty = (float) (y + (lado - fm.getHeight()) / 2 + fm.getAscent());
            g2.drawString(texto, tx, ty);
        }
    }

    private void pintarLineas(Graphics2D g2, GeometriaTablero geo) {
        g2.setStroke(new BasicStroke((float) geo.grosorLinea(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (LineaVisual linea : modelo.getLineas()) {
            g2.setColor(linea.paleta() == null ? Styles.COLOR_LINEA_HUERFANA : linea.paleta().getFuerte());
            dibujarArista(g2, geo, linea.arista());
        }
    }

    private void pintarPreview(Graphics2D g2, GeometriaTablero geo) {
        Arista preview = modelo.getLineaPreview();
        PaletaColor paleta = modelo.getPaletaTurno();
        if (preview != null && paleta != null) {
            g2.setColor(paleta.getPastel());
            dibujarArista(g2, geo, preview);
        }
    }

    private void dibujarArista(Graphics2D g2, GeometriaTablero geo, Arista arista) {
        g2.draw(new Line2D.Double(
                geo.x(arista.a().columna()), geo.y(arista.a().fila()),
                geo.x(arista.b().columna()), geo.y(arista.b().fila())));
    }

    private void pintarPuntos(Graphics2D g2, GeometriaTablero geo) {
        PaletaColor paleta = modelo.getPaletaTurno();
        Coordenada seleccion = modelo.getSeleccion();
        Set<Coordenada> candidatos = modelo.getCandidatos();
        Coordenada hover = modelo.getHover();

        for (int f = 0; f < geo.getFilas(); f++) {
            for (int c = 0; c < geo.getColumnas(); c++) {
                Coordenada punto = new Coordenada(f, c);
                Color color = Styles.COLOR_PUNTO_NEUTRO;
                double diametro = geo.diametroPunto();

                if (paleta != null) {
                    boolean enHover = punto.equals(hover);
                    if (punto.equals(seleccion)) {
                        color = paleta.getFuerte();
                        diametro = geo.diametroResaltado();
                    } else if (candidatos.contains(punto)) {
                        color = enHover ? paleta.getFuerte() : paleta.getPastel();
                        diametro = geo.diametroResaltado();
                    } else if (enHover) {
                        color = paleta.getPastel();
                        diametro = geo.diametroResaltado();
                    }
                }

                g2.setColor(color);
                g2.fill(new Ellipse2D.Double(geo.x(c) - diametro / 2, geo.y(f) - diametro / 2, diametro, diametro));
            }
        }
    }
}
