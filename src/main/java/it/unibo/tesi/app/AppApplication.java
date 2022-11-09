package it.unibo.tesi.app;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

	OrdineDiProduzioneDTO ordineDTO;

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

	private String data;

	@Scheduled(fixedDelay = 30000)
	public void getStatus(){
		data = "Applicazione attiva, ultima sincronizzazione: " + new Date();
		//sync()
	}

	@GetMapping(value="/stato")
	public String getMethodName() {
		return data;
	}
	
	public void sync()
	{

		final File folder = new File("/home/luca");
		ArrayList<File> fileXls = listXlsForFolder(folder);

		ArrayList<RigaExcel> righe = new ArrayList<>();
		ArrayList<RigaExcel> temp = new ArrayList<>();

		for (File xls : fileXls) {
			
			temp = leggiRigheXls(xls);

			righe.addAll(temp);
			
		}

		//System.out.println(righe.toString());
		
		ArrayList<String> OdPterminati = new ArrayList<>();
		
		for (RigaExcel riga : righe) {
			
			if (!OdPterminati.contains(riga.getOdp())) {
				
				if(isTerminato(riga.getOdp())){
					OdPterminati.add(riga.getOdp());
				}

			}
			
		}
		
		System.out.println(OdPterminati);

		ArrayList<OrdineDiProduzioneDTO> OdPdaAggiornare = leggiOdp(OdPterminati);

		ArrayList<OrdineDiProduzioneDTO> OdPCompleto = new ArrayList<>();


		//creo una scheda collaudo per ogni odp
		for (OrdineDiProduzioneDTO odp : OdPdaAggiornare) {
			
			SchedaControlloDTO scheda = new SchedaControlloDTO(schedaNextCode(), "FLOW_TEST", "Odp di origine: " + odp.getNumeroOdP());

			ArrayList<ControlloDTO> controlli = new ArrayList<>();
			ControlloDTO controllo;

			//associo a ogni scheda i suoi controlli
			for (RigaExcel riga : righe) {
				
				if(riga.getOdp().compareTo(odp.getNumeroOdP())==0)
				{
					if(riga.getEsito().compareTo("ABORT")!=0)
					{
						controllo = new ControlloDTO(scheda.getCodice(), Integer.parseInt(riga.getProgressivo()), riga.getEsito());

						if(controllo.isKO())
						{
							odp.incrementaScarti();
						}

						controlli.add(controllo);

						//la data riportata sulla scheda è quella indicata dall'ultimo controllo
						scheda.setDataEsito(riga.getData());
					}
				}

			}

			//aggiungo la lista di controlli alla scheda
			scheda.setControlli(controlli);

			//aggiungo la scheda all'odp
			odp.setSchedaControllo(scheda);

			//aggiungo l'odp completo a una lista di odp completi
			OdPCompleto.add(odp);

			//aggiungo l'odp al DB
			aggiungiOdPCompleto(odp);
			
		}

	}

	//restituisce lista di file .xls in una cartella
	public ArrayList<File> listXlsForFolder(final File folder) {

		ArrayList<File> result = new ArrayList<>();

		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()) {
				
				if(fileEntry.getName().endsWith(".xls"))
				{
					//System.out.println(fileEntry.getName());
					
					result.add(fileEntry);
				}
			}
		}

		return result;
	}

	//prende un file e trasforma le righe excel in oggetti RigaExcel
	public ArrayList<RigaExcel> leggiRigheXls(File xls){

		ArrayList<RigaExcel> result = new ArrayList<>();

		try {
			
			FileInputStream fis = new FileInputStream(xls);
			Workbook workbook = new HSSFWorkbook(fis);

			//prendo il primo foglio del file
			Sheet sheet = workbook.getSheetAt(0);

			String ArticleCode;
			Date StartTestTime;
			String Result;

			boolean intestazione = true;
			
			for (Row row : sheet) {

				if(!intestazione)
				{
					ArticleCode = row.getCell(2).getStringCellValue();
					StartTestTime = row.getCell(3).getDateCellValue();
					Result = row.getCell(27).getStringCellValue();
	
					result.add(new RigaExcel(ArticleCode, StartTestTime, Result));
	
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

	//controlla se l'odp è terminato
	public boolean isTerminato(String numero_odp)
	{
		DAOFactory daoFactoryInstance = DAOFactory.getDAOFactory();
		OrdineDiProduzioneDAO ordineDAO = daoFactoryInstance.getOrdineDiProduzioneDAO();

		if(ordineDAO.isTerminato(numero_odp))
		{
			return true;
		}
		else
			return false;

	}

	//legge tutti gli odp data una lista di codici odp
	public ArrayList<OrdineDiProduzioneDTO> leggiOdp(ArrayList<String> lista_codici)
	{
		ArrayList<OrdineDiProduzioneDTO> result = new ArrayList<>();

		DAOFactory daoFactoryInstance = DAOFactory.getDAOFactory();
		OrdineDiProduzioneDAO ordineDAO = daoFactoryInstance.getOrdineDiProduzioneDAO();

		for (String numero_odp : lista_codici) {
			
			result.add(ordineDAO.read(numero_odp));
	
		}
		
		return result;

	}

	//legge il prossimo numero di scheda
	public int schedaNextCode(){

		DAOFactory daoFactoryInstance = DAOFactory.getDAOFactory();
		SchedaControlloDAO schedaDAO = daoFactoryInstance.getSchedaControlloDAO();

		return schedaDAO.nextCode();

	}

	public void aggiungiOdPCompleto(OrdineDiProduzioneDTO odp)
	{

		DAOFactory daoFactoryInstance = DAOFactory.getDAOFactory();
		SchedaControlloDAO schedaDAO = daoFactoryInstance.getSchedaControlloDAO();
		OrdineDiProduzioneDAO ordineDAO = daoFactoryInstance.getOrdineDiProduzioneDAO();
		ControlloDAO controlloDAO = daoFactoryInstance.getControlloDAO();

		SchedaControlloDTO scheda = odp.getSchedaControllo();

		//aggiungo la scheda al DB
		schedaDAO.create(scheda);

		//aggiungo i controlli al DB
		for (ControlloDTO controlloDTO : scheda.getControlli()) {
			controlloDAO.create(controlloDTO);
		}

		//aggiorna l'odp
		ordineDAO.update(odp);

	}

}
