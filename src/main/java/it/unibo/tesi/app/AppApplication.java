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

	private String data = "Applicazione attiva, sincronizzazione non ancora eseguita.";
	private ArrayList<String> odpElaborati = new ArrayList<>();

	private final String crono = "0 30 12,17 ? * MON-FRI *";

	@GetMapping(value = "/stato")
	public String getMethodName() {
		
		String result;

		result = data + "\n\n";

		result = "Ultimi OdP elaborati: ";

		for (String odp : odpElaborati) {
			result = result + odp + ", ";
		}
		
		return result;
	}

	private final int esitoControlloTerminato = 124715008;

	@Scheduled(cron = crono)
	public void sync() throws FileNotFoundException {

		SettingsUtility settings = new SettingsUtility();
		final File folder = new File(settings.getFolderXls());
		
		XlsUtility fileXls = new XlsUtility(folder);

		ArrayList<RigaExcel> righe = fileXls.leggiRigheExcel();

		System.out.println(righe.toString());

		//lista in cui metto gli odp con stato "Terminato"
		ArrayList<String> OdPterminati = new ArrayList<>();

		for (RigaExcel riga : righe) {

			if (!OdPterminati.contains(riga.getOdp())) {

				if (isTerminato(riga.getOdp())) {
					OdPterminati.add(riga.getOdp());
				}
			}
		}

		//creo un oggetto OrdineDiProduzioneDTO per ogni odp terminato letto negli excel
		ArrayList<OrdineDiProduzioneDTO> OdPdaAggiornare = leggiOdp(OdPterminati);

		ArrayList<OrdineDiProduzioneDTO> OdPCompleto = new ArrayList<>();

		// creo una scheda collaudo per ogni odp
		for (OrdineDiProduzioneDTO odp : OdPdaAggiornare) {

			SchedaControlloDTO scheda = new SchedaControlloDTO(schedaNextCode(), settings.getCodiceScheda(),
					"Odp di origine: " + odp.getNumeroOdP(), esitoControlloTerminato);

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

		}

		// elimino gli odp trasferiti
		fileXls.removeOdPdaXls(OdPterminati);

		data = "Applicazione attiva, ultima sincronizzazione: " + new Date();
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


	//DA TOGLIERE

	// restituisce lista di file .xls in una cartella
	public ArrayList<File> listXlsForFolder(final File folder) {

		ArrayList<File> result = new ArrayList<>();

		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()) {

				if (fileEntry.getName().endsWith(".xls")) {
					// System.out.println(fileEntry.getName());

					result.add(fileEntry);
				}
			}
		}

		return result;
	}

	// prende un file e trasforma le righe excel in oggetti RigaExcel
	public ArrayList<RigaExcel> leggiRigheXls(File xls) {

		ArrayList<RigaExcel> result = new ArrayList<RigaExcel>();

		RigaExcel riga;

		try {

			FileInputStream fis = new FileInputStream(xls);
			Workbook workbook = new HSSFWorkbook(fis);

			// prendo il primo foglio del file
			Sheet sheet = workbook.getSheetAt(0);

			String ArticleCode;
			Date StartTestTime;
			String Result;

			boolean intestazione = true;

			for (Row row : sheet) {

				if (!intestazione) {
					ArticleCode = row.getCell(2).getStringCellValue();
					StartTestTime = row.getCell(3).getDateCellValue();
					Result = row.getCell(27).getStringCellValue();

					riga = new RigaExcel(ArticleCode, StartTestTime, Result);
					result.add(riga);

				}

				intestazione = false;

			}

			workbook.close();

			return result;

		} catch (Exception e) {
			System.out.println("Errore in lettura file " + xls.getName() + " : " + e.getLocalizedMessage());
		}

		return result;
	}

	// elimina le righe contenenti un certo ODP
	public static int removeOdPdaXls(File fileXls, String numero_odp) {
		int result = 0;

		try {

			FileInputStream fis = new FileInputStream(fileXls);
			Workbook workbookOrig = new HSSFWorkbook(fis);
			Workbook workbookDest = new HSSFWorkbook();

			// prendo il primo foglio del file origine
			Sheet sheetOrig = workbookOrig.getSheetAt(0);

			// creo un foglio nel file destinazione
			Sheet sheetDest = workbookDest.createSheet();

			Row nuovaRiga;
			int righeCopiate = 0;

			for (Row row : sheetOrig) {

				System.out.println(row.getCell(2));

				if (row.getCell(2) != null) {

					if (!row.getCell(2).getStringCellValue().startsWith(numero_odp) || row.getRowNum() == 0) {

						nuovaRiga = sheetDest.createRow(righeCopiate);

						for (Cell cell : row) {

							int i = cell.getColumnIndex();

							Cell oldCell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

							if (oldCell == null) {
								nuovaRiga.createCell(i).setCellValue("");
							} else if (oldCell.getCellType() == CellType.NUMERIC) {
								nuovaRiga.createCell(i).setCellValue(oldCell.getDateCellValue());
							} else if (oldCell.getCellType() == CellType.BOOLEAN) {
								nuovaRiga.createCell(i).setCellValue(oldCell.getBooleanCellValue());
							} else {
								nuovaRiga.createCell(i).setCellValue(oldCell.getStringCellValue());
							}

						}

						righeCopiate++;

					}
				}
			}

			workbookOrig.close();

			FileOutputStream fileOut = null;
			try {
				fileOut = new FileOutputStream(fileXls);
				workbookDest.write(fileOut);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				fileOut.close();
			}

			workbookDest.close();

		} catch (Exception e) {
			System.out.println("Errore eliminazione righe dell'Odp: " + numero_odp + ", " + e.getLocalizedMessage());
		}

		return result;

	}

}
