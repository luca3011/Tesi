package it.unibo.tesi.app;

public class ControlloDTO {

    private int codiceScheda;

    private int numeroRiga;

    private String esito;

    private String codiceControllo;

    public ControlloDTO(int codiceScheda, int numeroRiga, String esito, String codiceControllo) {
        this.codiceScheda = codiceScheda;
        this.numeroRiga = numeroRiga;
        this.esito = esito;
        this.codiceControllo = codiceControllo;
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

    public boolean isKO()
    {
        if(esito.compareTo("KO")==0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public String getCodiceControllo() {
        return codiceControllo;
    }

}
