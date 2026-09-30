package vista.juego;

import java.awt.FlowLayout;
import javax.swing.JPanel;
import vista.componentes.BtnRedondeado;
import vista.util.Styles;

public class PnlContenedorMenu extends JPanel {

    public PnlContenedorMenu(Runnable abrirOpciones) {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        BtnRedondeado btnOpciones = new BtnRedondeado("OPCIONES", Styles.COLOR_BOTON_FONDO, Styles.COLOR_BOTON_TEXTO);
        btnOpciones.addActionListener(e -> abrirOpciones.run());
        add(btnOpciones);
    }
}
