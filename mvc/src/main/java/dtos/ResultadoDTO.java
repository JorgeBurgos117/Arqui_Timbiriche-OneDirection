package dtos;

import java.awt.Color;

public class ResultadoDTO {

    private int posicion;
    private String nombre;
    private Color color;
    private int puntaje;
    private boolean abandono;
    private boolean ganador;

    public ResultadoDTO() {
    }

    public ResultadoDTO(int posicion, String nombre, Color color, int puntaje, boolean abandono, boolean ganador) {
        this.posicion = posicion;
        this.nombre = nombre;
        this.color = color;
        this.puntaje = puntaje;
        this.abandono = abandono;
        this.ganador = ganador;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public boolean isAbandono() {
        return abandono;
    }

    public void setAbandono(boolean abandono) {
        this.abandono = abandono;
    }

    public boolean isGanador() {
        return ganador;
    }

    public void setGanador(boolean ganador) {
        this.ganador = ganador;
    }
}
