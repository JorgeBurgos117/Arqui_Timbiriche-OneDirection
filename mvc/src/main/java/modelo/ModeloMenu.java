package modelo;

import java.util.ArrayList;
import java.util.List;
import juego.dominio.TamanioTablero;

public class ModeloMenu {

    public static final List<String> NOMBRES_PROVISIONALES = List.of("Asereje", "Ja", "Deje", "Dejebere");

    public List<OpcionPartida> getOpciones() {
        List<OpcionPartida> opciones = new ArrayList<>();
        for (TamanioTablero t : TamanioTablero.values()) {
            opciones.add(new OpcionPartida(t.getJugadores(), t.getPuntosPorLado()));
        }
        return opciones;
    }

    public List<String> getNombresProvisionales(int jugadores) {
        return NOMBRES_PROVISIONALES.subList(0, jugadores);
    }
}
