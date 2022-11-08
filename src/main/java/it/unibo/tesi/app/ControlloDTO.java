package it.unibo.tesi.app;

public class ControlloDTO {

    private int codiceScheda;

    private int numeroRiga;

    private String esito;

    public ControlloDTO(int codiceScheda, int numeroRiga, String esito) {
        this.codiceScheda = codiceScheda;
        this.numeroRiga = numeroRiga;
        this.esito = esito;
    }

    public int getCodiceScheda() {
        return codiceScheda;
    }

    public int getNumeroRiga() {
        return numeroRiga;
    }

    public String getEsito() {
        return esito;
    }


}
