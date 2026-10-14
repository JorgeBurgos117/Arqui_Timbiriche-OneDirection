package vista.juego;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import presentacion.dtos.JugadorDTO;
import vista.componentes.PnlAvatar;
import vista.util.Styles;
import vista.util.Tonos;
import vista.util.Ubicacion;

public class PnlJugador extends JPanel {

    private static final int SEPARACION_BORDE = 8;   // medidas "de diseño" (cuando el grosor es 150)
    private static final int SEPARACION_AVATAR = 4;

    private final Ubicacion ubicacion;
    private JugadorDTO datos;

    public PnlJugador(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
        setOpaque(false);
        int grosor = Styles.GROSOR_PANEL_JUGADOR;
        setPreferredSize(switch (ubicacion) {
            case NORTE, SUR -> new Dimension(grosor * 2, grosor);
            case ESTE, OESTE -> new Dimension(grosor, grosor * 2);
        });
    }

    public void actualizar(JugadorDTO jugador) {
        this.datos = jugador;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (datos == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int r = (ubicacion == Ubicacion.NORTE || ubicacion == Ubicacion.SUR) ? h : w;   // radio = grosor actual
        double k = r / (double) Styles.GROSOR_PANEL_JUGADOR;                            // factor de escala

        // ---- medio círculo: en turno color fuerte; si no, pastel
        Color fuerte = datos.getColor();
        g2.setColor(datos.isEnTurno() ? fuerte : Tonos.pastel(fuerte));
        switch (ubicacion) {
            case NORTE -> g2.fillArc(w / 2 - r, -r, r * 2, r * 2, 180, 180);
            case SUR -> g2.fillArc(w / 2 - r, h - r, r * 2, r * 2, 0, 180);
            case OESTE -> g2.fillArc(-r, h / 2 - r, r * 2, r * 2, -90, 180);
            case ESTE -> g2.fillArc(w - r, h / 2 - r, r * 2, r * 2, 90, 180);
        }

        // ---- medidas proporcionales
        int d = (int) Math.round(Styles.DIAMETRO_AVATAR * k);
        int sep = (int) Math.round(SEPARACION_AVATAR * k);
        int borde = (int) Math.round(SEPARACION_BORDE * k);
        Font fNombre = Styles.FUENTE_NOMBRE.deriveFont((float) (Styles.FUENTE_NOMBRE.getSize2D() * k));
        Font fPuntos = Styles.FUENTE_PUNTOS_ETIQUETA.deriveFont((float) (13 * k));
        FontMetrics mn = g2.getFontMetrics(fNombre);
        FontMetrics mp = g2.getFontMetrics(fPuntos);
        String nombre = datos.getNombre();
        String puntos = Styles.TEXTO_PUNTOS + " " + datos.getPuntaje();

        // columna: avatar + nombre + puntos (en el SUR el avatar va abajo)
        int altoTextos = mn.getHeight() + mp.getHeight();
        int altoCol = d + sep + altoTextos;
        int anchoCol = Math.max(d, Math.max(mn.stringWidth(nombre), mp.stringWidth(puntos)));

        // dónde va la columna: pegada al lado de la ventana, dentro del medio círculo
        int cx, top;
        switch (ubicacion) {
            case NORTE -> { cx = w / 2; top = borde; }
            case SUR -> { cx = w / 2; top = h - borde - altoCol; }
            case OESTE -> { cx = borde + anchoCol / 2; top = (h - altoCol) / 2; }
            default -> { cx = w - borde - anchoCol / 2; top = (h - altoCol) / 2; }   // ESTE
        }

        int yAvatar, yTextos;
        if (ubicacion == Ubicacion.SUR) {
            yTextos = top;
            yAvatar = top + altoTextos + sep;
        } else {
            yAvatar = top;
            yTextos = top + d + sep;
        }
        PnlAvatar.dibujar(g2, cx - d / 2, yAvatar, d, Tonos.claro(fuerte), fuerte, fuerte);

        // Sobre el color fuerte (turno) el texto va en blanco; sobre el pastel, en el tono oscuro
        g2.setColor(datos.isEnTurno() ? Styles.COLOR_BOTON_TEXTO : Tonos.oscuro(fuerte));
        g2.setFont(fNombre);
        g2.drawString(nombre, cx - mn.stringWidth(nombre) / 2, yTextos + mn.getAscent());
        g2.setFont(fPuntos);
        g2.drawString(puntos, cx - mp.stringWidth(puntos) / 2, yTextos + mn.getHeight() + mp.getAscent());

        g2.dispose();
    }
}