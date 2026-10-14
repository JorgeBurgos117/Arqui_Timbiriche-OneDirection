package vista.juego;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import javax.swing.JFrame;
import javax.swing.JPanel;
import vista.util.Styles;
import vista.util.Ubicacion;

/** Ventana de UN jugador: el tablero al centro y los jugadores alrededor. */
public class FrmJuego extends JFrame {

    public FrmJuego(String nombreJugador, VistaTablero vistaTablero, VistaJugadores vistaJugadores) {
        super(Styles.TITULO_VENTANA + " - " + nombreJugador);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel contenido = new JPanel(new DistribucionJuego(vistaTablero, vistaJugadores));
        contenido.setBackground(Styles.COLOR_FONDO);
        contenido.add(vistaTablero);
        for (Ubicacion u : Ubicacion.values()) {
            contenido.add(vistaJugadores.getPanel(u));
        }
        setContentPane(contenido);
    }

    /** indice: para que las ventanas de cada jugador no salgan una encima de otra. */
    public void mostrar(int indice) {
        pack();
        setMinimumSize(new Dimension(420, 440));   // para que no se pueda hacer tan chica que ya no se vea nada
        setLocation(40 + indice * 60, 20 + indice * 40);
        setVisible(true);
    }

    /**
     * Acomoda los medios círculos pegados a cada lado de la ventana y el tablero al centro.
     * Todo se escala con el mismo factor, así se ve igual en una ventana grande o chica.
     */
    private static class DistribucionJuego implements LayoutManager {

        private static final int G = Styles.GROSOR_PANEL_JUGADOR;                                   // 150
        private static final int AREA = Styles.LADO_AREA_TABLERO + 2 * Styles.PADDING_AREA_TABLERO; // 580
        private static final int DISENO = 2 * G + AREA;                                             // 880

        private final VistaTablero tablero;
        private final VistaJugadores jugadores;

        DistribucionJuego(VistaTablero tablero, VistaJugadores jugadores) {
            this.tablero = tablero;
            this.jugadores = jugadores;
        }

        @Override
        public void layoutContainer(Container c) {
            int w = c.getWidth();
            int h = c.getHeight();
            double k = Math.min(w, h) / (double) DISENO;      // factor de escala
            int g = (int) Math.round(G * k);                   // grosor (radio) de los medios círculos

            jugadores.getPanel(Ubicacion.NORTE).setBounds(w / 2 - g, 0, 2 * g, g);
            jugadores.getPanel(Ubicacion.SUR).setBounds(w / 2 - g, h - g, 2 * g, g);
            jugadores.getPanel(Ubicacion.OESTE).setBounds(0, h / 2 - g, g, 2 * g);
            jugadores.getPanel(Ubicacion.ESTE).setBounds(w - g, h / 2 - g, g, 2 * g);

            // el tablero: cuadrado, centrado y sin encimarse con los medios círculos
            int pad = (int) Math.round(Styles.PADDING_AREA_TABLERO * k);
            int lado = Math.max(50, Math.min(w, h) - 2 * g - 2 * pad);
            tablero.setBounds((w - lado) / 2, (h - lado) / 2, lado, lado);
        }

        @Override
        public Dimension preferredLayoutSize(Container c) {
            return new Dimension(DISENO, DISENO);
        }

        @Override
        public Dimension minimumLayoutSize(Container c) {
            return new Dimension(DISENO / 3, DISENO / 3);
        }

        @Override
        public void addLayoutComponent(String nombre, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }
    }
}