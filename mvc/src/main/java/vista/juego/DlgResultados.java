package vista.juego;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.FilaResultado;
import vista.componentes.BtnRedondeado;
import vista.util.Styles;

public class DlgResultados extends JDialog {

    public DlgResultados(Window propietario, String textoFin, List<FilaResultado> resultados) {
        super(propietario, "Fin de la partida", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        int m = Styles.MARGEN_DLG;

        JLabel lblTitulo = new JLabel("<html><div style='width:320px'>" + textoFin + "</div></html>");
        lblTitulo.setFont(Styles.FUENTE_DLG_NEGRITA);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(m, m, m / 2, m));

        DefaultTableModel datos = new DefaultTableModel(new Object[]{"Lugar", "Jugador", "Puntos"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        for (FilaResultado f : resultados) {
            String lugar = f.abandono() ? "—" : f.posicion() + "°";
            String nombre = f.nombre() + (f.ganador() ? "  (ganador)" : "");
            String puntos = f.abandono() ? "Abandonó" : String.valueOf(f.puntos());
            datos.addRow(new Object[]{lugar, nombre, puntos});
        }

        JTable tabla = new JTable(datos);
        tabla.setFont(Styles.FUENTE_DLG);
        tabla.setRowHeight(28);
        tabla.setFocusable(false);
        tabla.setRowSelectionAllowed(false);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getColumn(0).setMaxWidth(70);
        tabla.getColumnModel().getColumn(2).setMaxWidth(110);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(110);
        tabla.setDefaultRenderer(Object.class, new RenderResultado(resultados));

        tabla.setPreferredScrollableViewportSize(new Dimension(380, tabla.getPreferredSize().height));
        JScrollPane scroll = new JScrollPane(tabla);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Styles.COLOR_FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(0, m, 0, m));
        centro.add(scroll, BorderLayout.CENTER);

        BtnRedondeado btnMenu = new BtnRedondeado("VOLVER AL MENÚ", Styles.COLOR_BOTON_FONDO, Styles.COLOR_BOTON_TEXTO);
        btnMenu.addActionListener(e -> dispose());
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sur.setBackground(Styles.COLOR_FONDO);
        sur.setBorder(BorderFactory.createEmptyBorder(m / 2, m, m, m));
        sur.add(btnMenu);

        getContentPane().setBackground(Styles.COLOR_FONDO);
        setLayout(new BorderLayout());
        add(lblTitulo, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnMenu);
        pack();
        setResizable(false);
        setLocationRelativeTo(propietario);
    }

    private static final class RenderResultado extends DefaultTableCellRenderer {

        private final List<FilaResultado> filas;

        RenderResultado(List<FilaResultado> filas) {
            this.filas = filas;
        }

        @Override
        public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            FilaResultado f = filas.get(row);
            setHorizontalAlignment(column == 1 ? SwingConstants.LEFT : SwingConstants.CENTER);
            setForeground(f.abandono() ? Styles.COLOR_TEXTO_SECUNDARIO : f.paleta().getOscuro());
            setBackground(f.ganador() ? f.paleta().getClaro() : Styles.COLOR_FONDO);
            setFont(f.ganador() ? getFont().deriveFont(Font.BOLD) : getFont());
            return this;
        }
    }
}
