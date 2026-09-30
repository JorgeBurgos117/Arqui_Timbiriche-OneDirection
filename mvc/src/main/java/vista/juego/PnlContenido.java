package vista.juego;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import modelo.JugadorVisual;
import modelo.Ubicacion;
import vista.util.Styles;

public class PnlContenido extends JLayeredPane {

    private final JPanel pnlJuego = new JPanel(new BorderLayout());
    private final PnlContenedorMenu pnlContenedorMenu;
    private final Map<Integer, PnlJugador> panelesJugador = new LinkedHashMap<>();

    public PnlContenido(List<JugadorVisual> jugadores, PnlTablero pnlTablero, Runnable abrirOpciones) {
        this.pnlContenedorMenu = new PnlContenedorMenu(abrirOpciones);

        pnlJuego.setBackground(Styles.COLOR_FONDO);
        pnlJuego.setOpaque(true);

        int pad = Styles.PADDING_AREA_TABLERO;
        JPanel areaTablero = new JPanel(new GridBagLayout());
        areaTablero.setOpaque(false);
        areaTablero.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
        areaTablero.add(pnlTablero);
        pnlJuego.add(areaTablero, BorderLayout.CENTER);

        boolean hayNorte = false;
        for (JugadorVisual jugador : jugadores) {
            PnlJugador panel = new PnlJugador(jugador.ubicacion());
            panel.actualizar(jugador);
            panelesJugador.put(jugador.id(), panel);
            pnlJuego.add(panel, aBorderLayout(jugador.ubicacion()));
            hayNorte |= jugador.ubicacion() == Ubicacion.NORTE;
        }
        if (!hayNorte) {
            int alto = pnlContenedorMenu.getPreferredSize().height + Styles.MARGEN_MENU * 2;
            pnlJuego.add(Box.createVerticalStrut(alto), BorderLayout.NORTH);
        }

        setOpaque(false);
        add(pnlJuego, JLayeredPane.DEFAULT_LAYER);
        add(pnlContenedorMenu, JLayeredPane.PALETTE_LAYER);
    }

    public void actualizarJugadores(List<JugadorVisual> jugadores) {
        Map<Integer, JugadorVisual> porId = new LinkedHashMap<>();
        jugadores.forEach(j -> porId.put(j.id(), j));
        panelesJugador.forEach((id, panel) -> {
            JugadorVisual j = porId.get(id);
            if (j == null) {
                panel.marcarAusente();
            } else {
                panel.actualizar(j);
            }
        });
    }

    private static String aBorderLayout(Ubicacion ubicacion) {
        return switch (ubicacion) {
            case NORTE -> BorderLayout.NORTH;
            case SUR -> BorderLayout.SOUTH;
            case ESTE -> BorderLayout.EAST;
            case OESTE -> BorderLayout.WEST;
        };
    }

    @Override
    public void doLayout() {
        pnlJuego.setBounds(0, 0, getWidth(), getHeight());
        Dimension d = pnlContenedorMenu.getPreferredSize();
        pnlContenedorMenu.setBounds(Styles.MARGEN_MENU, Styles.MARGEN_MENU, d.width, d.height);
    }

    @Override
    public Dimension getPreferredSize() {
        return pnlJuego.getPreferredSize();
    }
}
