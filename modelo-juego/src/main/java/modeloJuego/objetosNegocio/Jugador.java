package modeloJuego.objetosNegocio;

public class Jugador {

    private final String nombre;
    private final String avatar;
    private final String color;
    private int puntaje;
    private boolean activo = true;

    public Jugador(String nombre, String avatar, String color) {
        this.nombre = nombre;
        this.avatar = avatar;
        this.color = color;
    }

    public void agregarPuntaje(int puntos) {
        puntaje += puntos;
    }

    public void abandonar() {
        activo = false;
    }

    public String getNombre() {
        return nombre;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getColor() {
        return color;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public boolean isActivo() {
        return activo;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
