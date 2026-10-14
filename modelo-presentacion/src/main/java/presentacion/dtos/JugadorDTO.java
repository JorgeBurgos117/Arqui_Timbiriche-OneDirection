package presentacion.dtos;

import java.awt.Color;

public class JugadorDTO {

    private String nombre;
    private String avatar;
    private Color color;
    private int puntaje;
    private boolean enTurno;

    public JugadorDTO(String nombre, String avatar, Color color, int puntaje, boolean enTurno) {
        this.nombre = nombre;
        this.avatar = avatar;
        this.color = color;
        this.puntaje = puntaje;
        this.enTurno = enTurno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public boolean isEnTurno() {
        return enTurno;
    }

    public void setEnTurno(boolean enTurno) {
        this.enTurno = enTurno;
    }
}
