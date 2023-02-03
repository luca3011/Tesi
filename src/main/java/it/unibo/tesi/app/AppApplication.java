package it.unibo.tesi.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableScheduling
@RestController
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}
	
	private String statoApplicazione;
	private ArrayList<String> odpElaborati = new ArrayList<>();
	private boolean syncEseguito = false;

	private final String crono = "0 30 12,17 ? * MON-FRI";

	@GetMapping(value = "/stato")
	public String frontend() throws FileNotFoundException {
		
		String result;

		if(syncEseguito == true)
		{
			result = statoApplicazione + "\n\n";
	
			result = "Ultimi OdP elaborati: ";
	
			for (String odp : odpElaborati) {
				result = result + odp + ", ";
			}
			
			return result;
		}
		else
		{
			return "Applicazione attiva, sincronizzazione non ancora eseguita.";
		}

	}

	//per test
	@GetMapping(value = "/forceUpdate")
	public String forceUpdate() throws FileNotFoundException {
		
		sync();

		return "Update forzato";
	}


	@Scheduled(cron = crono)
	public void sync() throws FileNotFoundException {

		SettingsUtility settings = new SettingsUtility();
		final File folder = new File(settings.getFolderXls());
		
		XlsUtility fileXls = new XlsUtility(folder);

		ArrayList<RigaExcel> righe = fileXls.leggiRigheExcel();

		System.out.println("Lette " + righe.size() + " righe in totale.");

		//lista in cui metto gli odp con stato "Terminato"
		ArrayList<String> OdPterminati = new ArrayList<>();

		for (RigaExcel riga : righe) {

			if (!OdPterminati.contains(riga.getOdp())) {

				if (isTerminato(riga.getOdp())) {
					OdPterminati.add(riga.getOdp());
				}
			}
		}

		System.out.println("Odp terminati da elaborare: " + OdPterminati.toString());

		//creo un oggetto OrdineDiProduzioneDTO per ogni odp terminato letto negli excel
		ArrayList<OrdineDiProduzioneDTO> OdPdaAggiornare = leggiOdp(OdPterminati);

		ArrayList<OrdineDiProduzioneDTO> OdPCompleto = new ArrayList<>();

		// creo una scheda collaudo per ogni odp
		for (OrdineDiProduzioneDTO odp : OdPdaAggiornare) {

			SchedaControlloDTO scheda = new SchedaControlloDTO(schedaNextCode(), settings.getCodiceScheda(),
					"Odp di origine: " + odp.getNumeroOdP(), settings.getEsitoControlloTerminato());

			ArrayList<ControlloDTO> controlli = new ArrayList<>();
			ControlloDTO controllo;

			// associo a ogni scheda i suoi controlli
			for (RigaExcel riga : righe) {

				if (riga.getOdp().compareTo(odp.getNumeroOdP()) == 0) {

					//ignoro le righe con esito "ABORT"
					if (riga.getEsito().compareTo("ABORT") != 0) {
						controllo = new ControlloDTO(scheda.getCodice(), Integer.parseInt(riga.getProgressivo()),
								riga.getEsito(), settings.getCodiceControllo());

						//se il controllo ha esito KO vi è uno scarto in più nell'odp
						if (controllo.isKO()) {
							odp.incrementaScarti();
						}

						controlli.add(controllo);

						// la data riportata sulla scheda è quella indicata dall'ultimo controllo
						scheda.setDataEsito(riga.getData());
					}
				}
			}

			// aggiungo la lista di controlli alla scheda
			scheda.setControlli(controlli);

			// aggiungo la scheda all'odp
			odp.setSchedaControllo(scheda);

			// aggiungo l'odp completo a una lista di odp completi
			OdPCompleto.add(odp);

			// aggiungo l'odp con al DB
			aggiungiOdPCompleto(odp);

			System.out.println("Elaborato Odp numero:" + odp.getNumeroOdP());

		}

		// elimino gli odp trasferiti
		int righeEliminate;
		righeEliminate = fileXls.removeOdPdaXls(OdPterminati);
		System.out.println("Eliminate " + righeEliminate + "righe in totale.");

		syncEseguito = true;
		statoApplicazione = "Applicazione attiva, ultima sincronizzazione: " + new Date();
		odpElaborati = OdPterminati;

	}

	// controlla se l'odp è terminato
	public boolean isTerminato(String numero_odp) {
		DAOFactory daoFactoryInstance = DAOFactory.getDAOFactory();
		OrdineDiProduzioneDAO ordineDAO = daoFactoryInstance.getOrdineDiProduzioneDAO();

		if (ordineDAO.isTerminato(numero_odp)) {
			return true;
		} else
			return false;

	}

	// legge tutti gli odp data una lista di codici odp
	public ArrayList<OrdineDiProduzioneDTO> leggiOdp(ArrayList<String> lista_codici) {
		ArrayList<OrdineDiProduzioneDTO> result = new ArrayList<>();

		DAOFactory daoFactoryInstance = DAOFactory.getDAOFactory();
		OrdineDiProduzioneDAO ordineDAO = daoFactoryInstance.getOrdineDiProduzioneDAO();

		for (String numero_odp : lista_codici) {

			result.add(ordineDAO.read(numero_odp));

		}

		return result;

	}

	// mette sul DB odp, scheda e controlli
	public void aggiungiOdPCompleto(OrdineDiProduzioneDTO odp) {

		DAOFactory daoFactoryInstance = DAOFactory.getDAOFactory();
		SchedaControlloDAO schedaDAO = daoFactoryInstance.getSchedaControlloDAO();
		OrdineDiProduzioneDAO ordineDAO = daoFactoryInstance.getOrdineDiProduzioneDAO();
		ControlloDAO controlloDAO = daoFactoryInstance.getControlloDAO();

		SchedaControlloDTO scheda = odp.getSchedaControllo();

		// aggiungo la scheda al DB
		schedaDAO.create(scheda);

		// aggiungo i controlli al DB
		for (ControlloDTO controlloDTO : scheda.getControlli()) {
			controlloDAO.create(controlloDTO);
		}

		// aggiorna l'odp
		ordineDAO.update(odp);

	}

	//restituisce il codice della scheda nuova da creare
	public int schedaNextCode()
	{
		DAOFactory daoFactoryInstance = DAOFactory.getDAOFactory();
		SchedaControlloDAO schedaDAO = daoFactoryInstance.getSchedaControlloDAO();
		
		return schedaDAO.nextCode();

	}

}
