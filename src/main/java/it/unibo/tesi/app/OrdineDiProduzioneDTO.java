package it.unibo.tesi.app;

public class OrdineDiProduzioneDTO {

    private int idOdp;

    private String numeroOdP;

    private String codiceArticolo;

    private int stato;

    private int scarti;

    private SchedaControlloDTO schedaControllo;

    public OrdineDiProduzioneDTO() {
        this.idOdp = 0;
        this.numeroOdP = "";
        this.codiceArticolo = "";
        this.stato = 0;
        this.scarti = 0;
        this.schedaControllo = new SchedaControlloDTO(0, "", "", 0);
        ;
    }

    public String getNumeroOdP() {
        return numeroOdP;
    }

    public int getScarti() {
        return scarti;
    }

    public void incrementaScarti() {
        this.scarti++;
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

    @Override
    public String toString() {
        return "OrdineDiProduzioneDTO [idOdp=" + idOdp + ", numeroOdP=" + numeroOdP + ", codiceArticolo="
                + codiceArticolo + ", stato=" + stato + ", scarti=" + scarti + ", schedaControllo=" + schedaControllo
                + "]";
    }

}
