package vista;

import dtos.JugadorDTO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import vista.componentes.BtnRedondeado;
import vista.util.PaletaColor;
import vista.util.Styles;

public class DlgColores extends JDialog {

    private final Map<String, JComboBox<PaletaColor>> combos = new LinkedHashMap<>();
    private final Predicate<Map<String, Color>> aplicar;

    public DlgColores(Window propietario, List<JugadorDTO> jugadores, Predicate<Map<String, Color>> aplicar) {
        super(propietario, "Colores del tablero", ModalityType.APPLICATION_MODAL);
        this.aplicar = aplicar;

        int m = Styles.MARGEN_DLG;
        JPanel filas = new JPanel(new GridLayout(0, 2, 12, 10));
        filas.setBackground(Styles.COLOR_FONDO);
        filas.setBorder(BorderFactory.createEmptyBorder(m, m, m / 2, m));
        for (JugadorDTO j : jugadores) {
            JLabel lbl = new JLabel(j.getNombre());
            lbl.setFont(Styles.FUENTE_DLG_NEGRITA);
            JComboBox<PaletaColor> combo = new JComboBox<>(PaletaColor.DISPONIBLES.toArray(new PaletaColor[0]));
            combo.setSelectedItem(PaletaColor.porColor(j.getColor()));
            combo.setRenderer(new RenderPaleta());
            combos.put(j.getNombre(), combo);
            filas.add(lbl);
            filas.add(combo);
        }

        BtnRedondeado btnAceptar = new BtnRedondeado("ACEPTAR", Styles.COLOR_BOTON_FONDO, Styles.COLOR_BOTON_TEXTO);
        BtnRedondeado btnCancelar = new BtnRedondeado("CANCELAR", Styles.COLOR_BOTON_NEUTRO, Styles.COLOR_BOTON_TEXTO);
        btnAceptar.addActionListener(e -> aceptar());
        btnCancelar.addActionListener(e -> dispose());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setBackground(Styles.COLOR_FONDO);
        botones.setBorder(BorderFactory.createEmptyBorder(m / 2, m, m, m));
        botones.add(btnCancelar);
        botones.add(btnAceptar);

        setLayout(new BorderLayout());
        add(filas, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        pack();
        setResizable(false);
        setLocationRelativeTo(propietario);
    }

    private void aceptar() {
        Map<String, Color> seleccion = new LinkedHashMap<>();
        combos.forEach((nombre, combo) -> {
            PaletaColor paleta = (PaletaColor) combo.getSelectedItem();
            seleccion.put(nombre, paleta == null ? null : paleta.getColor());
        });
        if (aplicar.test(seleccion)) {
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Cada jugador debe tener un color distinto.",
                    "Colores repetidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static final class RenderPaleta extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof PaletaColor paleta) {
                setText(paleta.getNombre());
                setIcon(new IconoColor(paleta.getColor()));
            }
            return this;
        }
    }

    private record IconoColor(Color color) implements Icon {

        private static final int TAM = 14;

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillOval(x, y, TAM, TAM);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return TAM;
        }

        @Override
        public int getIconHeight() {
            return TAM;
        }
    }
}
