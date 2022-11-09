package it.unibo.tesi.app;

import java.util.ArrayList;
import java.util.Date;

public class SchedaControlloDTO {
    
    private int codice;

    private String moduloControllo;

    private Date dataEsito;

    private String note;

    private ArrayList<ControlloDTO> controlli;

    private int esitoControllo;

    public SchedaControlloDTO(int codice, String moduloControllo, String note, int esitoControllo) {
        this.codice = codice;
        this.moduloControllo = moduloControllo;
        this.note = note;
        this.controlli = new ArrayList<>();
        this.esitoControllo = esitoControllo;
    }

    public int getCodice() {
        return codice;
    }

    public String getModuloControllo() {
        return moduloControllo;
    }

    public Date getDataEsito() {
        return dataEsito;
    }

    public String getNote() {
        return note;
    }

    public void setDataEsito(Date dataEsito) {
        this.dataEsito = dataEsito;
    }

    public ArrayList<ControlloDTO> getControlli() {
        return controlli;
    }

    public void setControlli(ArrayList<ControlloDTO> controlli) {
        this.controlli = controlli;
    }

    public void addControllo(ControlloDTO controllo){
        this.controlli.add(controllo);
    }

    public int getEsitoControllo() {
        return esitoControllo;
    }


}
