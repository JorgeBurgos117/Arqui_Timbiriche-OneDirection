package presentacion.dtos;

import java.awt.*;

public class FiguraDTO {

    private TipoFigura tipo;
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private Color color;

    public FiguraDTO(TipoFigura tipo, int x1, int y1, int x2, int y2, Color color) {
        this.tipo = tipo;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
    }

    public String getClave() {
        if (tipo == TipoFigura.PUNTO) {
            return tipo + ":" + x1 + "," + y1;
        }
        return tipo + ":" + x1 + "," + y1 + "," + x2 + "," + y2;
    }

    public TipoFigura getTipo() {
        return tipo;
    }

    public void setTipo(TipoFigura tipo) {
        this.tipo = tipo;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
