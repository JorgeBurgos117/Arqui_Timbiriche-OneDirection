package dtos;

public class VotacionDTO {

    private String votante;
    private String proponente;

    public VotacionDTO() {
    }

    public VotacionDTO(String votante, String proponente) {
        this.votante = votante;
        this.proponente = proponente;
    }

    public String getVotante() {
        return votante;
    }

    public void setVotante(String votante) {
        this.votante = votante;
    }

    public String getProponente() {
        return proponente;
    }

    public void setProponente(String proponente) {
        this.proponente = proponente;
    }
}
