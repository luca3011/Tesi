package it.unibo.tesi.app;

import java.util.ArrayList;
import java.util.Date;

public class SchedaControlloDTO {
    
    private int codice;

    private String moduloControllo;

    private Date dataEsito;

    private String note;

    private ArrayList<ControlloDTO> controlli;

    public SchedaControlloDTO(int codice, String moduloControllo, Date dataEsito, String note) {
        this.codice = codice;
        this.moduloControllo = moduloControllo;
        this.dataEsito = dataEsito;
        this.note = note;
        this.controlli = new ArrayList<>();
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

    public ArrayList<ControlloDTO> getControlli() {
        return controlli;
    }

    public void addControllo(ControlloDTO controllo){
        this.controlli.add(controllo);
    }


}
