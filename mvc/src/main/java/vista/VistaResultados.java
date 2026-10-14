package vista;

import dtos.ResultadoDTO;
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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modeloPresentacion.EventoPresentacion;
import modeloPresentacion.IConsultaModeloPresentacion;
import modeloPresentacion.ObservadorPresentacion;
import vista.componentes.BtnRedondeado;
import vista.util.PaletaColor;
import vista.util.Styles;

public class VistaResultados implements ObservadorPresentacion {

    private IConsultaModeloPresentacion modelo;
    private Window propietario;
    private final Runnable alCerrar;
    private boolean mostrada = false;

    public VistaResultados(Runnable alCerrar) {
        this.alCerrar = alCerrar;
    }

    public void setModelo(IConsultaModeloPresentacion modelo) {
        this.modelo = modelo;
    }

    public void setPropietario(Window propietario) {
        this.propietario = propietario;
    }

    @Override
    public void actualizar(EventoPresentacion evento) {
        if (evento != EventoPresentacion.PARTIDA_TERMINADA || mostrada) {
            return;
        }
        mostrada = true;
        String texto = modelo.getTextoFin();
        List<ResultadoDTO> resultados = modelo.getResultados();
        SwingUtilities.invokeLater(() -> {
            construir(texto, resultados).setVisible(true);
            alCerrar.run();
        });
    }

    private JDialog construir(String textoFin, List<ResultadoDTO> resultados) {
        JDialog dialogo = new JDialog(propietario, "Fin de la partida", JDialog.ModalityType.APPLICATION_MODAL);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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
        for (ResultadoDTO r : resultados) {
            datos.addRow(new Object[]{
                r.isAbandono() ? "—" : r.getPosicion() + "°",
                r.getNombre() + (r.isGanador() ? "  (ganador)" : ""),
                r.isAbandono() ? "Abandonó" : String.valueOf(r.getPuntaje())
            });
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

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Styles.COLOR_FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(0, m, 0, m));
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);

        BtnRedondeado btnMenu = new BtnRedondeado("VOLVER AL MENÚ", Styles.COLOR_BOTON_FONDO, Styles.COLOR_BOTON_TEXTO);
        btnMenu.addActionListener(e -> dialogo.dispose());
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sur.setBackground(Styles.COLOR_FONDO);
        sur.setBorder(BorderFactory.createEmptyBorder(m / 2, m, m, m));
        sur.add(btnMenu);

        dialogo.getContentPane().setBackground(Styles.COLOR_FONDO);
        dialogo.setLayout(new BorderLayout());
        dialogo.add(lblTitulo, BorderLayout.NORTH);
        dialogo.add(centro, BorderLayout.CENTER);
        dialogo.add(sur, BorderLayout.SOUTH);
        dialogo.getRootPane().setDefaultButton(btnMenu);
        dialogo.pack();
        dialogo.setResizable(false);
        dialogo.setLocationRelativeTo(propietario);
        return dialogo;
    }

    private static final class RenderResultado extends DefaultTableCellRenderer {

        private final List<ResultadoDTO> filas;

        RenderResultado(List<ResultadoDTO> filas) {
            this.filas = filas;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            ResultadoDTO r = filas.get(row);
            setHorizontalAlignment(column == 1 ? SwingConstants.LEFT : SwingConstants.CENTER);
            setForeground(r.isAbandono() ? Styles.COLOR_TEXTO_SECUNDARIO : PaletaColor.oscuro(r.getColor()));
            setBackground(r.isGanador() ? PaletaColor.claro(r.getColor()) : Styles.COLOR_FONDO);
            setFont(r.isGanador() ? getFont().deriveFont(Font.BOLD) : getFont());
            return this;
        }
    }
}
