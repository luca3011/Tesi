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

	private String data;

	@Scheduled(fixedDelay = 300000)
	public void getStatus() {
		data = "Applicazione attiva, ultima sincronizzazione: " + new Date();
		sync();
	}

	@GetMapping(value = "/stato")
	public String getMethodName() {
		return data;
	}

	private String folderXls = "C:\\Users\\Luca\\Desktop";
	//private String doneFolderXls = "C:\\Users\\Luca\\Desktop";
	private String codiceScheda = "FLOW_TEST";
	private String codiceControllo = "OK_FLOW";
	private int esitoControlloTerminato = 124715008;

	//routine principale
	public void sync() {

		final File folder = new File(folderXls);
		ArrayList<File> fileXls = listXlsForFolder(folder);

		ArrayList<RigaExcel> righe = new ArrayList<>();
		ArrayList<RigaExcel> temp = new ArrayList<>();

		for (File xls : fileXls) {

			temp = leggiRigheXls(xls);

			righe.addAll(temp);

		}

		System.out.println(righe.toString());

		ArrayList<String> OdPterminati = new ArrayList<>();

		for (RigaExcel riga : righe) {

			if (!OdPterminati.contains(riga.getOdp())) {

				if (isTerminato(riga.getOdp())) {
					OdPterminati.add(riga.getOdp());
				}
			}
		}

		ArrayList<OrdineDiProduzioneDTO> OdPdaAggiornare = leggiOdp(OdPterminati);

		ArrayList<OrdineDiProduzioneDTO> OdPCompleto = new ArrayList<>();

		// creo una scheda collaudo per ogni odp
		for (OrdineDiProduzioneDTO odp : OdPdaAggiornare) {

			SchedaControlloDTO scheda = new SchedaControlloDTO(schedaNextCode(), codiceScheda,
					"Odp di origine: " + odp.getNumeroOdP(), esitoControlloTerminato);

			ArrayList<ControlloDTO> controlli = new ArrayList<>();
			ControlloDTO controllo;

			// associo a ogni scheda i suoi controlli
			for (RigaExcel riga : righe) {

				if (riga.getOdp().compareTo(odp.getNumeroOdP()) == 0) {
					if (riga.getEsito().compareTo("ABORT") != 0) {
						controllo = new ControlloDTO(scheda.getCodice(), Integer.parseInt(riga.getProgressivo()),
								riga.getEsito(), codiceControllo);

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

			// aggiungo l'odp al DB
			aggiungiOdPCompleto(odp);

		}

		// elimino gli odp trasferiti

		for (OrdineDiProduzioneDTO odp : OdPCompleto) {
			for (File xls : fileXls) {
				removeOdPdaXls(xls, odp.getNumeroOdP());
			}
		}

	}

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

	// legge il prossimo numero di scheda
	public int schedaNextCode() {

		DAOFactory daoFactoryInstance = DAOFactory.getDAOFactory();
		SchedaControlloDAO schedaDAO = daoFactoryInstance.getSchedaControlloDAO();

		return schedaDAO.nextCode() + 1;

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
