package mensajeria;

import java.time.LocalDateTime;

public class Mensaje implements IMensajeApp, IMensajeAdmon {

    private String remitente;
    private String destinatario;
    private String contenido;
    private LocalDateTime fechaHora;
    private String ipOrigen;
    private int puertoOrigen;
    private String ipDestino;
    private int puertoDestino;

    public Mensaje() {
    }

    @Override
    public String getRemitente() {
        return remitente;
    }

    @Override
    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    @Override
    public String getDestinatario() {
        return destinatario;
    }

    @Override
    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    @Override
    public String getContenido() {
        return contenido;
    }

    @Override
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    @Override
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    @Override
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    @Override
    public String getIpOrigen() {
        return ipOrigen;
    }

    @Override
    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    @Override
    public int getPuertoOrigen() {
        return puertoOrigen;
    }

    @Override
    public void setPuertoOrigen(int puertoOrigen) {
        this.puertoOrigen = puertoOrigen;
    }

    @Override
    public String getIpDestino() {
        return ipDestino;
    }

    @Override
    public void setIpDestino(String ipDestino) {
        this.ipDestino = ipDestino;
    }

    @Override
    public int getPuertoDestino() {
        return puertoDestino;
    }

    @Override
    public void setPuertoDestino(int puertoDestino) {
        this.puertoDestino = puertoDestino;
    }

    @Override
    public String toString() {
        return remitente + " -> " + destinatario + ": " + contenido;
    }
}
