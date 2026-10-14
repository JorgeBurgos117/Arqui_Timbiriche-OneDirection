package juego.objetosNegocio;

import java.util.Objects;

public class Jugador {

    private final int id;
    private final String nombre;
    private final String avatar;
    private int puntaje;

    public Jugador(int id, String nombre, String avatar) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre);
        this.avatar = avatar;
    }

    public void agregarPuntaje(int puntos) {
        this.puntaje += puntos;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getPuntaje() {
        return puntaje;
    }

    // El id es el identificador del jugador
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Jugador otro)) return false;
        return id == otro.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return nombre + " (" + puntaje + ")";
    }
}
