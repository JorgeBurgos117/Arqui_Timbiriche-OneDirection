package modelo;

//datos mínimos, completar
public record DatosJugador(int id, String nombre, PaletaColor paleta) {

    public char inicial() {
        return nombre == null || nombre.isBlank()
                ? '?'
                : Character.toUpperCase(nombre.charAt(0));
    }
}