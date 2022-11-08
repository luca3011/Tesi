package it.unibo.tesi.app;

public class OrdineDiProduzioneDTO {

    private int idOdp;

    private String numeroOdP;

    private String codiceArticolo;

    private int stato;

    private int scarti;

    private SchedaControlloDTO schedaControllo;

    public OrdineDiProduzioneDTO(int idOdp, String numeroOdP, String codiceArticolo, int stato) {
        this.idOdp = idOdp;
        this.numeroOdP = numeroOdP;
        this.codiceArticolo = codiceArticolo;
        this.stato = stato;
        this.scarti = 0;
        this.schedaControllo = null;
    }

    public OrdineDiProduzioneDTO() {
        this.idOdp = 0;
        this.numeroOdP = "";
        this.codiceArticolo = "";
        this.stato = 0;
        this.scarti = 0;
        this.schedaControllo = null;
    }
    
    public int getIdOdp() {
        return idOdp;
    }

    public String getNumeroOdP() {
        return numeroOdP;
    }

    public String getCodiceArticolo() {
        return codiceArticolo;
    }

    public int getStato() {
        return stato;
    }

    public int getScarti() {
        return scarti;
    }

    public void setScarti(int scarti) {
        this.scarti = scarti;
    }

    public void setSchedaControllo(SchedaControlloDTO schedaControllo) {
        this.schedaControllo = schedaControllo;
    }

    public SchedaControlloDTO getSchedaControllo() {
        return schedaControllo;
    }

    public void setIdOdp(int idOdp) {
        this.idOdp = idOdp;
    }

    public void setNumeroOdP(String numeroOdP) {
        this.numeroOdP = numeroOdP;
    }

    public void setCodiceArticolo(String codiceArticolo) {
        this.codiceArticolo = codiceArticolo;
    }

    public void setStato(int stato) {
        this.stato = stato;
    }


}
