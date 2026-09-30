package juego.red;

import juego.dominio.Accion;

public class BitacoraConsola implements IObservadorRed {

    @Override
    public void update(Accion accion) {
        String texto = switch (accion) {
            case Accion.SeleccionarPunto s -> "P" + s.idJugador() + " selecciona " + s.punto();
            case Accion.CancelarSeleccion c -> "P" + c.idJugador() + " cancela su seleccion";
            case Accion.TrazarLinea t -> "P" + t.idJugador() + " traza " + t.a() + "-" + t.b();
            case Accion.Abandonar a -> "P" + a.idJugador() + " abandona la partida";
            case Accion.ProponerFin p -> "P" + p.idJugador() + " propone terminar la partida";
            case Accion.VotarFin v -> "P" + v.idJugador() + (v.acepta() ? " acepta" : " rechaza") + " terminar";
        };
        System.out.println("[RED] -> " + texto);
    }
}
