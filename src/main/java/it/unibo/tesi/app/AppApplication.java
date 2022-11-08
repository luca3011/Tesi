package it.unibo.tesi.app;

import java.util.Date;

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

	private String OdP = "";
	OrdineDiProduzioneDTO ordineDTO;

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

	private String data;

	@Scheduled(fixedDelay = 30000)
	public void getStatus(){
		data = "Applicazione attiva, ultima sincronizzazione: " + new Date();
		sync();
	}

	@GetMapping(value="/stato")
	public String getMethodName() {
		return data;
	}
	
	public void sync()
	{

		DAOFactory daoFactoryInstance = DAOFactory.getDAOFactory();

		OrdineDiProduzioneDAO ordineDAO = daoFactoryInstance.getOrdineDiProduzioneDAO();
		SchedaControlloDAO schedaDAO = daoFactoryInstance.getSchedaControlloDAO();
		ControlloDAO controlloDAO = daoFactoryInstance.getControlloDAO();

		OrdineDiProduzioneDTO ordineDTO = ordineDAO.read("04/00005");
		

		
	}



}
