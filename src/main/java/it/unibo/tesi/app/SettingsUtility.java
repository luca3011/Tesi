package it.unibo.tesi.app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.PluggableSchemaResolver;
import org.springframework.context.annotation.Bean;

public class SettingsUtility {

    private String folderXls = "C:\\Users\\Luca\\Desktop";
    private String codiceScheda = "FLOW_TEST";
    private String codiceControllo = "OK_FLOW";
    private final int esitoControlloTerminato = 124715008;

    public SettingsUtility() {

        try {

            String path = "./properties.txt";
            FileInputStream fis = new FileInputStream(path);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            String line = in.readLine();

            while (line != null) {

                if (!line.startsWith("#")) {
                    String[] line_split = line.split(" ");

                    if (line_split.length == 3) {
                        switch (line_split[0]) {
                            case "folderXls":
                                this.folderXls = line_split[2];
                                break;
                            case "codiceScheda":
                                this.codiceScheda = line_split[2];
                                break;
                            case "codiceControllo":
                                this.codiceControllo = line_split[2];
                                break;
                        
                            default:
                                throw new FileNotFoundException();
                        }
                    }

                }

                line = in.readLine();
            }

        } catch (Exception e) {
            System.err.println("Errore lettura file impostazioni: " + e.getLocalizedMessage());
        }

    }

    public String getFolderXls() {
        return folderXls;
    }

    public String getCodiceScheda() {
        return codiceScheda;
    }

    public String getCodiceControllo() {
        return codiceControllo;
    }

    public int getEsitoControlloTerminato() {
        return esitoControlloTerminato;
    }

    

}
