package vista.componentes;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import vista.util.Styles;

//contenedor para botón de opciones

public class PnlContenedorMenu extends JPanel {

    public interface EscuchaMenu {

        void abrirOpciones();
    }

    private final BtnRedondeado btnOpciones;

    public PnlContenedorMenu(EscuchaMenu escucha) {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        btnOpciones = new BtnRedondeado("OPCIONES", Styles.COLOR_BOTON_FONDO, Styles.COLOR_BOTON_TEXTO);
        btnOpciones.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (escucha != null) {
                    escucha.abrirOpciones();
                }
            }
        });
        add(btnOpciones);
    }

    public BtnRedondeado getBtnOpciones() {
        return btnOpciones;
    }
}