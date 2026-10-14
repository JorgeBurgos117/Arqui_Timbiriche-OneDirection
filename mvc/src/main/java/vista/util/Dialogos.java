package vista.util;

import java.awt.Component;
import java.awt.Dialog;
import java.util.function.Consumer;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

public final class Dialogos {

    private Dialogos() {
    }

    public static void preguntar(Component padre, String mensaje, String titulo, int tipoMensaje,
                                 Consumer<Boolean> alResponder) {
        JOptionPane panel = new JOptionPane(mensaje, tipoMensaje, JOptionPane.YES_NO_OPTION);
        JDialog dialogo = panel.createDialog(padre, titulo);
        dialogo.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
        boolean[] respondido = {false};
        panel.addPropertyChangeListener(JOptionPane.VALUE_PROPERTY, e -> {
            if (respondido[0] || panel.getValue() == JOptionPane.UNINITIALIZED_VALUE) {
                return;
            }
            respondido[0] = true;
            dialogo.dispose();
            alResponder.accept(Integer.valueOf(JOptionPane.YES_OPTION).equals(panel.getValue()));
        });
        dialogo.setVisible(true);
    }

    public static void avisar(Component padre, String mensaje, String titulo, int tipoMensaje) {
        JOptionPane panel = new JOptionPane(mensaje, tipoMensaje);
        JDialog dialogo = panel.createDialog(padre, titulo);
        dialogo.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
        dialogo.setVisible(true);
        dialogo.dispose();
    }
}
