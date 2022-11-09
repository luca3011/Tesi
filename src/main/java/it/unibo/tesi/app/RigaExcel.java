package it.unibo.tesi.app;

import java.util.Date;

public class RigaExcel {

    private String articleCode;

    private Date data;

    private String esito;

    private String Odp;

    private String progressivo;

    public RigaExcel(String articleCode, Date data, String esito) {
        this.articleCode = articleCode;
        this.data = data;
        this.esito = esito;

        String[] parti;
        parti = this.articleCode.split(";");
        this.Odp = parti[0];
        this.progressivo = parti[1];
    }

    public String getArticleCode() {
        return articleCode;
    }

    public Date getData() {
        return data;
    }

    public String getEsito() {
        return esito;
    }

    public String getOdp() {
        return Odp;
    }

    public String getProgressivo() {
        return progressivo;
    }

    @Override
    public String toString() {
        return "RigaExcel [articleCode=" + articleCode + ", data=" + data + ", esito=" + esito + ", Odp=" + Odp
                + ", progressivo=" + progressivo + "]";
    }

    
}
